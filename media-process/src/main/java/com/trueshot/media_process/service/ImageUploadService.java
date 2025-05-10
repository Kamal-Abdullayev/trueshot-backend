package com.trueshot.media_process.service;

import com.trueshot.media_process.constants.HelperConstant;
import com.trueshot.media_process.constants.MinioConfigConstant;
import com.trueshot.media_process.dto.ImageListResponseDto;
import com.trueshot.media_process.dto.ImageResponseDto;
import com.trueshot.media_process.dto.ImageSaveRequestDto;
import com.trueshot.media_process.exception.IncompleteProcessException;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageUploadService {
    private final MinioConfigConstant minioConfigConstant;
    private final MinioClient minioClient;
    private final HelperConstant projectHelperConstant;

    public ImageListResponseDto saveImage(ImageSaveRequestDto imageSaveRequestDto) {
        String encoded = imageSaveRequestDto.getImageContent();
        if (!StringUtils.hasText(encoded)) {
            log.error("Image content is null or empty");
            throw new IllegalArgumentException("Image content is required");
        }

        log.info("Decoding image content");
        byte[] imageContent;
        try {
            imageContent = Base64.getDecoder()
                    .decode(encoded.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            log.error("Failed to decode image content: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid image format");
        }

        String imagePathForFileStorageSystem = UUID.randomUUID() + "/"
                + System.currentTimeMillis() + "_" + imageSaveRequestDto.getImageName() + ".jpg";

        String imageMinioPath = saveImageToMinio(imagePathForFileStorageSystem, imageContent);
        if (imageMinioPath == null) {
            throw new IllegalStateException("Failed to upload image to storage");
        }

        String imagePath = projectHelperConstant.getBaseUrl()
                + projectHelperConstant.getImageFolderPath() + "/" + imageMinioPath;

        log.info("Image full path: {}", imagePath);
        return new ImageListResponseDto(imagePath);
    }

    public String saveImageToMinio(String imagePath, byte[] imageContent) {
        log.info("Image will upload to MinIO bucket with \"{}\" path", imagePath);
        try {
            checkIfBucketNotExistCreate();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfigConstant.getBucketName())
                    .object(imagePath)
                    .stream(new ByteArrayInputStream(imageContent),
                            imageContent.length, -1)
                    .build()
            );
            log.info("Image successfully saved to MinIO bucket: {}", minioConfigConstant.getBucketName());
            return imagePath;
        } catch (ErrorResponseException e) {
            log.error("Error response from MinIO for image '{}': {}", imagePath, e.errorResponse().message());
            return null;
        } catch (InsufficientDataException e) {
            log.error("Insufficient data for image '{}': {}", imagePath, e.getMessage());
            return null;
        } catch (ServerException e) {
            log.error("Server error while uploading image '{}': {}", imagePath, e.getMessage());
            return null;
        } catch (IOException | InternalException | InvalidKeyException |
                 InvalidResponseException | NoSuchAlgorithmException | XmlParserException e) {
            log.error("General error while uploading image '{}': {}", imagePath, e.getMessage());
            return null;
        }
    }

    public ImageResponseDto readFileFromMinio(String folderPath, String imageName) {
        if (!StringUtils.hasText(imageName)) {
            log.warn("Image name is null or empty, returning default image");
            // Optionally load default image bytes or return a reference
            return new ImageResponseDto(null, 0);
        }
        String imagePath = folderPath + "/" + imageName;
        log.info("Reading image from MinIO bucket at path: {}", imagePath);
        checkIfBucketNotExistCreate();
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfigConstant.getBucketName())
                        .object(imagePath)
                        .build());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageData = outputStream.toByteArray();
            log.info("Image successfully read from MinIO bucket: {}", minioConfigConstant.getBucketName());
            return new ImageResponseDto(new ByteArrayResource(imageData), imageData.length);

        } catch (MinioException | IOException e) {
            log.error("Error occurred while retrieving image '{}': {}", imagePath, e.getMessage());
            return new ImageResponseDto(null, 0);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Security error while retrieving image '{}': {}", imagePath, e.getMessage());
            return new ImageResponseDto(null, 0);
        }
    }

    private void checkIfBucketNotExistCreate() {
        log.info("Checking if bucket '{}' exists", minioConfigConstant.getBucketName());
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioConfigConstant.getBucketName()).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfigConstant.getBucketName()).build());
                log.info("Bucket '{}' created successfully.", minioConfigConstant.getBucketName());
            }
        } catch (Exception e) {
            log.error("Failed to create or check bucket '{}': {}", minioConfigConstant.getBucketName(), e.getMessage());
            throw new IncompleteProcessException("Error while creating bucket: " + minioConfigConstant.getBucketName());
        }
    }
}
