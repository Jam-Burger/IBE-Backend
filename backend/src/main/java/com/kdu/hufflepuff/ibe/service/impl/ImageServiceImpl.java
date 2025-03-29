package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ConfigUpdateException;
import com.kdu.hufflepuff.ibe.exception.ImageUploadException;
import com.kdu.hufflepuff.ibe.exception.InvalidImageTypeException;
import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.LandingPageConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import com.kdu.hufflepuff.ibe.model.enums.ImageType;
import com.kdu.hufflepuff.ibe.service.interfaces.ImageService;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomTypeService;
import com.kdu.hufflepuff.ibe.service.interfaces.WebsiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private static final List<String> VALID_IMAGE_MIME_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml", "image/avif"
    );
    private final S3Client s3Client;
    private final WebsiteConfigService websiteConfigService;
    private final RoomTypeService roomTypeService;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.cloudfront.base-url}")
    private String cloudFrontBaseUrl;

    @Override
    public String uploadImage(MultipartFile file, ImageType imageType, Long tenantId) {
        if (imageType == ImageType.ROOM) {
            throw new IllegalArgumentException("Cannot upload room images using this method.");
        }

        try {
            validateImage(file);
            String path = String.format("%d/%s", tenantId, imageType.getPath());
            String fileUrl = uploadFileToS3(file, path);

            switch (imageType) {
                case LOGO:
                    updateLogoUrl(tenantId, fileUrl);
                    break;
                case BANNER:
                    updateBannerUrl(tenantId, fileUrl);
                    break;
                default:
                    break;
            }
            return fileUrl;
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload file to S3", e);
        }
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files, Long tenantId, Long roomTypeId) {
        List<String> imageUrls = new ArrayList<>();
        String path = String.format("%d/%s/%d", tenantId, ImageType.ROOM.getPath(), roomTypeId);
        try {
            for (MultipartFile file : files) {
                validateImage(file);
                String fileUrl = uploadFileToS3(file, path);
                imageUrls.add(fileUrl);
            }
            updateRoomImageUrls(tenantId, roomTypeId, imageUrls);
            return imageUrls;
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload multiple room images", e);
        }
    }

    /**
     * Validates that the uploaded file is actually an image
     *
     * @param file The file to validate
     * @throws InvalidImageTypeException if the file is not a valid image type
     */
    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (!VALID_IMAGE_MIME_TYPES.contains(contentType)) {
            throw new InvalidImageTypeException("Invalid file type. Only images are allowed: " + VALID_IMAGE_MIME_TYPES + ". Received: " + contentType);
        }
    }

    /**
     * Central method for uploading files to S3
     */
    private String uploadFileToS3(MultipartFile file, String path) throws IOException {
        String fileName = UUID.randomUUID().toString();
        String key = String.format("%s/%s", path, fileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.getContentType())
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(file.getBytes())));
        return String.format("%s/%s", cloudFrontBaseUrl, key);
    }

    private void updateLogoUrl(Long tenantId, String fileUrl) {
        try {
            WebsiteConfigModel config = websiteConfigService.getConfig(tenantId, ConfigType.GLOBAL);
            GlobalConfigModel globalConfig = config.getGlobalConfigModel();

            GlobalConfigModel.Brand brand = globalConfig.getBrand();
            brand.setLogoUrl(fileUrl);
            globalConfig.setBrand(brand);
            config.setGlobalConfigModel(globalConfig);

            ConfigRequestDTO<GlobalConfigModel> request = new ConfigRequestDTO<>();
            request.setConfig(globalConfig);
            websiteConfigService.saveConfig(tenantId, ConfigType.GLOBAL, request);
        } catch (Exception e) {
            throw new ConfigUpdateException("Failed to update logo URL in configuration", e);
        }
    }

    private void updateBannerUrl(Long tenantId, String fileUrl) {
        try {
            WebsiteConfigModel config = websiteConfigService.getConfig(tenantId, ConfigType.LANDING);
            LandingPageConfigModel landingConfig = config.getLandingPageConfigModel();

            LandingPageConfigModel.Banner banner = landingConfig.getBanner();
            banner.setImageUrl(fileUrl);
            banner.setEnabled(true);
            landingConfig.setBanner(banner);
            config.setLandingPageConfigModel(landingConfig);

            ConfigRequestDTO<LandingPageConfigModel> request = new ConfigRequestDTO<>();
            request.setConfig(landingConfig);
            websiteConfigService.saveConfig(tenantId, ConfigType.LANDING, request);
        } catch (Exception e) {
            throw new ConfigUpdateException("Failed to update banner URL in configuration", e);
        }
    }

    private void updateRoomImageUrls(Long tenantId, Long roomTypeId, List<String> fileUrls) {
        try {
            roomTypeService.updateRoomTypeImages(tenantId, roomTypeId, fileUrls);
        } catch (Exception e) {
            throw new ConfigUpdateException("Failed to update room URLs in configuration", e);
        }
    }
}
