package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.enums.ImageType;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/{tenantId}/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * Uploads a single image and validates it's a proper image type
     */
    @PostMapping("/{imageType}")
    public ResponseEntity<ApiResponse<String>> uploadImage(
        @Valid @PathVariable Long tenantId,
        @Valid @PathVariable ImageType imageType,
        @Valid @RequestParam("file") MultipartFile file
    ) throws IOException {
        String fileUrl = imageService.uploadImage(file, imageType, tenantId);

        return ApiResponse.<String>builder()
            .statusCode(HttpStatus.OK)
            .message("Image uploaded successfully")
            .data(fileUrl)
            .build()
            .send();
    }

    /**
     * Uploads multiple room images and validates they are proper image types
     */
    @PostMapping("/ROOM/{roomTypeId}")
    public ResponseEntity<ApiResponse<List<String>>> uploadRoomImages(
        @PathVariable Long tenantId,
        @PathVariable Long roomTypeId,
        @RequestParam("files") List<MultipartFile> files
    ) {
        List<String> fileUrls = imageService.uploadImages(files, tenantId, roomTypeId);

        return ApiResponse.<List<String>>builder()
            .statusCode(HttpStatus.OK)
            .message("Room images uploaded successfully")
            .data(fileUrls)
            .build()
            .send();
    }
}
