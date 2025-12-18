package com.edunext.app.services;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    private final S3Client s3Client;
    private final String bucketName;
    private final boolean isAwsConfigured;

    public StorageService(
            @Autowired(required = false) S3Client s3Client,
            @Value("${cloud.aws.bucket.name:#{null}}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;

        if (this.s3Client == null || this.bucketName == null || this.bucketName.isBlank()) {
            this.isAwsConfigured = false;
            logger.warn("WARN: AWS S3 não configurado. Uploads de arquivo não funcionarão");
        } else {
            this.isAwsConfigured = true;
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {

        if (!isAwsConfigured) {
            throw new IllegalStateException("Upload falhou: AWS S3 não está configurado");
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String key = "fotos-alunos/" + UUID.randomUUID() + fileExtension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        URL s3Url = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key));

        return s3Url.toString();
    }

    public String getFileExtension(String filename){
        if (filename == null || !filename.contains(".")){
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }

}
