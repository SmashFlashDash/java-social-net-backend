package ru.team38.userservice.services;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.gimpy.DropShadowGimpyRenderer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.account.CaptchaDto;
import ru.team38.userservice.exceptions.CaptchaCreationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final DefaultTextProducer textProducer;
    private final DefaultWordRenderer wordRenderer;
    private final GradiatedBackgroundProducer backgroundProducer;
    private final DropShadowGimpyRenderer gimpyRenderer;
    private final CacheManager cacheManager;

    @LoggingMethod
    public CaptchaDto createCaptcha() throws CaptchaCreationException {
        String captchaSecret = UUID.randomUUID().toString();

        Captcha captcha = new Captcha.Builder(200, 50)
                .addText(textProducer, wordRenderer)
                .addBackground(backgroundProducer)
                .gimp(gimpyRenderer)
                .addNoise()
                .build();

        String captchaSolution = captcha.getAnswer();
        BufferedImage captchaImage = captcha.getImage();

        CaptchaDto captchaDto = getCaptchaDto(captchaSecret, captchaImage);
        Cache cache = cacheManager.getCache("captchaCache");
        if (cache != null) {
            cache.put(captchaSecret, captchaSolution);
            log.info("Captcha created and stored in cache with secret: {}", captchaSecret);
        }
        return captchaDto;
    }

    @NotNull
    private static CaptchaDto getCaptchaDto(String captchaSecret, BufferedImage captchaImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(captchaImage, "png", baos);
        } catch (IOException e) {
            throw new CaptchaCreationException("Failed to encode captcha image to byte array.", e);
        }
        byte[] bytes = baos.toByteArray();
        String encodedImage = Base64.getEncoder().encodeToString(bytes);

        return new CaptchaDto(captchaSecret, "data:image/png;base64, " + encodedImage);
    }

    public boolean validateCaptcha(String captchaSecret, String captchaSolution) {
        Cache cache = cacheManager.getCache("captchaCache");
        if (cache != null) {
            String cachedCaptchaSolution = cache.get(captchaSecret, String.class);
            if (cachedCaptchaSolution == null) {
                log.error("Captcha expired for secret: {}", captchaSecret);
                return false;
            } else if (!cachedCaptchaSolution.equals(captchaSolution)) {
                log.error("Invalid captcha code for secret: {}", captchaSecret);
                return false;
            }
            return true;
        } else {
            log.error("Cache is null");
            return false;
        }
    }

    @Scheduled(fixedRateString = "#{${captcha.clearCacheRate} * 60000}")
    public void clearExpiredCaptchas() {
        Cache cache = cacheManager.getCache("captchaCache");
        if (cache != null) {
            log.info("Clearing captcha cache");
            cache.clear();
        }
    }
}