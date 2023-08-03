package ru.team38.communicationsservice.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.storage.FileDto;
import ru.team38.common.dto.storage.FileType;
import ru.team38.common.dto.storage.FileUriResponse;
import ru.team38.communicationsservice.data.repositories.StorageRepository;
import ru.team38.communicationsservice.exceptions.BadRequestException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {
    @Value("${yandexObjectStorage.bucketName}")
    private String bucketName;
    private final AmazonS3 yandexS3client;
    private final StorageRepository storageRepository;
    private final int COMPRESS_SIZE_THRESHOLD = 1_000_000;
    private final ThreadPoolExecutor imageExecutor;

    @Transactional
    @LoggingMethod
    public FileUriResponse uploadFile(FileType type, MultipartFile file) throws BadRequestException {
        final String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
        if (type.equals(FileType.IMAGE) && fileExt != null && !fileExt.matches("jpg|jpeg")) {
            throw new BadRequestException("Allows upload only jpeg format for images");
        }
        byte[] inputBytes;
        try {
            inputBytes = file.getBytes();
        } catch (IOException ex) {
            throw new BadRequestException("Error saving file: " + file.getOriginalFilename(), ex);
        }
        String filename = String.join(".", UUID.randomUUID().toString(), fileExt);
        FileDto fileDto = new FileDto(null, filename, file.getOriginalFilename());
        storeFile(inputBytes, filename, file);
        if (type == FileType.IMAGE && file.getSize() > COMPRESS_SIZE_THRESHOLD) {
            imageExecutor.execute(() -> compressAndStore(inputBytes, file, filename));
        }
        storageRepository.saveNewFileRecord(fileDto);
        return new FileUriResponse(yandexS3client.getUrl(bucketName, filename).toString());
    }

    private void storeFile(byte[] fileBytes, String filename, MultipartFile file) throws BadRequestException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(fileBytes.length);
        try {
            yandexS3client.putObject(bucketName, filename, new ByteArrayInputStream(fileBytes), metadata);
        } catch (Exception ex) {
            throw new BadRequestException("Error saving file: " + file.getOriginalFilename(), ex);
        }
    }

    private void compressAndStore(byte[] inputBytes, MultipartFile file, String filename) {
        final float koef = COMPRESS_SIZE_THRESHOLD / (float) file.getSize();
        try (ByteArrayInputStream input =  new ByteArrayInputStream(inputBytes); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            BufferedImage inputImage = ImageIO.read(input);
            Thumbnails.of(inputImage).scale(koef).outputFormat("jpg").toOutputStream(os);
            storeFile(os.toByteArray(), filename, file);
        } catch (IOException ex) {
            log.error("Error comprasing image: " + file.getOriginalFilename());
        } catch (BadRequestException ex) {
            log.error(ex.getMessage(), ex);
        }
    }


}


