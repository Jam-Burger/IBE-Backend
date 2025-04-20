package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.enums.ImageType;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.ImageService;
import com.kdu.hufflepuff.ibe.validation.PositiveLong;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/{tenantId}/images")
@RequiredArgsConstructor
@Tag(name = "Images", description = "Image upload and management API")
public class ImageController {

    private final ImageService imageService;

    @Operation(
        summary = "Upload single image",
        description = "Uploads a single image and validates it's a proper image type"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Image uploaded successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid image file",
            content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Error uploading image",
            content = @Content)
    })
    @PostMapping(value = "/{imageType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadImage(
        @Parameter(description = "ID of the tenant") @PositiveLong @PathVariable Long tenantId,
        @Parameter(description = "Type of image being uploaded") @Valid @PathVariable ImageType imageType,
        @Parameter(description = "Image file to upload") @Valid @RequestParam("file") MultipartFile file
    ) throws IOException {
        String fileUrl = imageService.uploadImage(file, imageType, tenantId);

        return ApiResponse.<String>builder()
            .statusCode(HttpStatus.OK)
            .message("Image uploaded successfully")
            .data(fileUrl)
            .build()
            .send();
    }

    @Operation(
        summary = "Upload multiple room images",
        description = "Uploads multiple room images and validates they are proper image types"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Room images uploaded successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid image files",
            content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Error uploading images",
            content = @Content)
    })
    @PostMapping(value = "/ROOM/{roomTypeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> uploadRoomImages(
        @Parameter(description = "ID of the tenant") @PositiveLong @PathVariable Long tenantId,
        @Parameter(description = "ID of the room type") @PathVariable Long roomTypeId,
        @Parameter(description = "Image files to upload") @RequestParam("files") List<MultipartFile> files
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