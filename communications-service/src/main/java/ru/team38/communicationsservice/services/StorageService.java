package ru.team38.communicationsservice.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${yandexObjectStorage.bucketName}")
    private String bucketName;
    private final AmazonS3 yandexS3client;
    private final StorageRepository storageRepository;

    @Transactional
    @LoggingMethod
    public FileUriResponse getUploadedFileUri(FileType type, MultipartFile file) throws BadRequestException {
        String uuidFileName = UUID.randomUUID().toString();
        FileDto fileDto = new FileDto(null, uuidFileName, file.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        try {
            storageRepository.saveNewFileRecord(fileDto);
            PutObjectResult result = yandexS3client.putObject(bucketName, uuidFileName, file.getInputStream(), metadata);
        } catch (Exception ex) {
            throw new BadRequestException("Error saving file: " + file.getOriginalFilename(), ex);
        }
        return new FileUriResponse(yandexS3client.getUrl(bucketName, uuidFileName).toString());
    }
}
