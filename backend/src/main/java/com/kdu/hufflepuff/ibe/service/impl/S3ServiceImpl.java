package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ConfigUpdateException;
import com.kdu.hufflepuff.ibe.exception.ImageUploadException;
import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.LandingPageConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import com.kdu.hufflepuff.ibe.model.enums.ImageType;
import com.kdu.hufflepuff.ibe.service.interfaces.S3Service;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;
    private final WebsiteConfigService websiteConfigService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.cloudfront.base-url}")
    private String cloudFrontBaseUrl;

    @Override
    public String uploadFile(MultipartFile file, ImageType imageType, Long tenantId, Long roomTypeId) {
        try {
            String fileName = UUID.randomUUID().toString();
            String key;

            if (imageType == ImageType.ROOM) {
                if (roomTypeId == null) {
                    throw new IllegalArgumentException("roomTypeId is required for room images");
                }
                key = String.format("%s/%d/%s", imageType.getPath(), roomTypeId, fileName);
            } else {
                key = String.format("%s/%s", imageType.getPath(), fileName);
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(file.getBytes())));
            String fileUrl = String.format("%s/%s", cloudFrontBaseUrl, key);

            switch (imageType) {
                case LOGO:
                    updateLogoUrl(tenantId, fileUrl);
                    break;
                case BANNER:
                    updateBannerUrl(tenantId, fileUrl);
                    break;
                case ROOM:
                    updateRoomUrl(tenantId, roomTypeId, fileUrl);
                    break;
            }

            return fileUrl;
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload file to S3", e);
        }
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

    private void updateRoomUrl(Long tenantId, Long roomTypeId, String fileUrl) {
        try {
            // TODO: Implement room image update in room type configuration
            // This will be implemented when we have the room type configuration model
        } catch (Exception e) {
            throw new ConfigUpdateException("Failed to update room URL in configuration", e);
        }
    }
}
