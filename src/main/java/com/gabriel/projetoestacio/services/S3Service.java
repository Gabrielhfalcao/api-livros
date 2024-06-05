package com.gabriel.projetoestacio.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(@Value("${AWS_ACCESS_KEY_ID}") String accessKeyId,
                     @Value("${AWS_SECRET_ACCESS_KEY}") String secretKey,
                     @Value("${AWS_REGION}") String region) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        File convertedFile = convertMultiPartToFile(file);
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
        convertedFile.delete();
        return fileName;
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    public Resource downloadFile(String fileName) throws MalformedURLException {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        Path tempFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve(fileName);
        File file = tempFile.toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            s3Object.getObjectContent().transferTo(fos);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao baixar o arquivo do S3", e);
        }
        return new UrlResource(file.toURI());
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
