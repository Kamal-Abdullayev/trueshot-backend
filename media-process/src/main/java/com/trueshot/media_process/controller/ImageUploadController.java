package com.trueshot.media_process.controller;

import com.trueshot.media_process.dto.ImageListResponseDto;
import com.trueshot.media_process.dto.ImageResponseDto;
import com.trueshot.media_process.dto.ImageSaveRequestDto;
import com.trueshot.media_process.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image")
public class ImageUploadController {
    private final ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestBody ImageSaveRequestDto imageSaveRequestDto) {
        try {
            ImageListResponseDto response = imageUploadService.saveImage(imageSaveRequestDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{folderPath}/{imageName}")
    public ResponseEntity<ByteArrayResource> getNewsByImageFolder(@PathVariable("folderPath") String folderPath,
                                                                  @PathVariable String imageName) {
        ImageResponseDto imageResponseDto = imageUploadService.readFileFromMinio(folderPath, imageName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(imageResponseDto.getContentLength())
                .body(imageResponseDto.getByteArrayResource());
    }
}
