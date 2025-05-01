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
    public ResponseEntity<ImageListResponseDto> uploadImage(@RequestBody ImageSaveRequestDto imageSaveRequestDto) {
        return new ResponseEntity<>(imageUploadService.saveImage(imageSaveRequestDto), HttpStatus.CREATED);
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
