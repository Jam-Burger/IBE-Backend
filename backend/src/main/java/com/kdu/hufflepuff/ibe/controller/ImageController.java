package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/{tenantId}/{configType}")
public class ImageController {

    private final S3Service s3Service;

    public ImageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
        @PathVariable ConfigType configType,

        @RequestParam("file") MultipartFile file
    ) {
        try {
            String fileUrl = s3Service.uploadFile(file);
            return ApiResponse.<String>builder()
                    .statusCode(HttpStatus.OK)
                    .message("File uploaded successfully")
                    .data(fileUrl)
                    .build()
                    .send(); // send() already returns ResponseEntity<ApiResponse<String>>
        } catch (IOException e) {
            return ApiResponse.<String>builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Error uploading file: " + e.getMessage())
                    .data(null)
                    .build()
                    .send();
        }
    }
}
