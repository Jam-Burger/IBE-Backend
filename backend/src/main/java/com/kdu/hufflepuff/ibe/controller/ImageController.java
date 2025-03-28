package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.enums.ImageType;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/{tenantId}/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @PostMapping("/{imageType}")
    public ResponseEntity<ApiResponse<String>> uploadImage(
        @Valid @PathVariable Long tenantId,
        @Valid @PathVariable ImageType imageType,
        @Valid @RequestParam("file") MultipartFile file,
        @Valid @RequestParam(name = "room_type_id", required = false) Long roomTypeId
    ) throws IOException {
        String fileUrl = s3Service.uploadFile(file, imageType, tenantId, roomTypeId);

        return ApiResponse.<String>builder()
            .statusCode(HttpStatus.OK)
            .message("File uploaded successfully")
            .data(fileUrl)
            .build()
            .send();
    }
}
