package ru.team38.communicationsservice.configs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.imageio.ImageIO;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class YandexStorageConfig {
    @Value("${yandexObjectStorage.accessKey}")
    private String yandexAccessKey;
    @Value("${yandexObjectStorage.secretKey}")
    private String yandexSecretKey;
    @Value("${yandexObjectStorage.bucketName}")
    private String bucketName;
    @Value("${yandexObjectStorage.endpoint}")
    private String endpoint;
    @Value("${yandexObjectStorage.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3Client() throws Exception {
        AWSCredentials credentials = new BasicAWSCredentials(yandexAccessKey, yandexSecretKey);
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        if (!s3client.doesBucketExist(bucketName)) {
            throw new Exception("Bucket \"" + bucketName + "\" not exist");
        }
        return s3client;
    }

    @Bean
    public ThreadPoolExecutor imageExecutor() {
        ImageIO.setUseCache(false);
        return (ThreadPoolExecutor) Executors.newCachedThreadPool(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
    }
}
