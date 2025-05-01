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

        String imagePathForFileStorageSystem = UUID.randomUUID() + "/"
                + System.currentTimeMillis() + "_" + imageSaveRequestDto.getImageName() + ".jpg";
        byte[] imageContent = null;
        imageContent = Base64.getDecoder()
                .decode(imageSaveRequestDto.getImageContent()
                                .getBytes(StandardCharsets.UTF_8));

        if (imageContent == null) {
            log.error("Failed to decode image content");
            throw new IncompleteProcessException("Failed to decode image content");
        }
        String imageMinioPath = saveImageToMinio(imagePathForFileStorageSystem, imageContent);
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

        } catch (ErrorResponseException e) {
            log.error("Error response from MinIO for image '{}': {}", imagePath, e.errorResponse().message());
            throw new IncompleteProcessException("MinIO responded with an error. Image upload failed.");

        } catch (InsufficientDataException e) {
            log.error("Insufficient data for image '{}': {}", imagePath, e.getMessage());
            throw new IncompleteProcessException("Upload failed due to insufficient data.");

        } catch (ServerException e) {
            log.error("Server error while uploading image '{}': {}", imagePath, e.getMessage());
            throw new IncompleteProcessException("Upload failed due to a server error.");

        } catch (IOException | InternalException | InvalidKeyException |
                 InvalidResponseException | NoSuchAlgorithmException | XmlParserException e) {
            log.error("General error while uploading image '{}': {}", imagePath, e.getMessage());
            throw new IncompleteProcessException("An error occurred while uploading the image.");
        }

        return imagePath;
    }

    public ImageResponseDto readFileFromMinio(String folderPath, String imageName) {
        log.info("Read image from MinIO bucket with \"{}\" path and \"{}\" name", folderPath, imageName);
        String imagePath =  folderPath + "/" + imageName;
        log.info("Image path: {}", imagePath);
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
            return new ImageResponseDto(new ByteArrayResource(imageData),
                    imageData.length);

        } catch (MinioException | IOException e) {
            log.error("Error occurred while retrieving the image \"'{}'\": {}", imagePath, e.getMessage());
            throw new IncompleteProcessException("Error occurred while retrieving the image");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error while uploading image to \"'{}'\": {}", imagePath, e.getMessage());
            throw new IncompleteProcessException("An error occurred while uploading the image.");
        }
    }

    private void checkIfBucketNotExistCreate() {
        log.info("Checking if bucket '{}' exist", minioConfigConstant.getBucketName());
        boolean found;
        try {
            String bucketName = minioConfigConstant.getBucketName();
            found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioConfigConstant.getBucketName()).build());
            if (found) {
                log.info("Bucket '{}' already exists.", bucketName);
            } else {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created successfully.", bucketName);
            }
        }
        catch (Exception e) {
            log.error("Failed to create a bucket: {}. Error: {}", minioConfigConstant.getBucketName(), e.getMessage());
            throw new IncompleteProcessException("Error while creating bucket: " + minioConfigConstant.getBucketName());
        }
    }

}
