package com.edunext.app.services;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class StorageService {
    private final S3Client s3Client;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    public StorageService(S3Client s3Client){
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String key = "fotos-alunos/" + UUID.randomUUID().toString() + fileExtension;

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
