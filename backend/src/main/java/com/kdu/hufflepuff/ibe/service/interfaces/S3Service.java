package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.enums.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    /**
     * Uploads a file to S3 and updates the corresponding configuration in DynamoDB if needed.
     *
     * @param file       The file to upload
     * @param imageType  The type of image (LOGO, BANNER, ROOM)
     * @param tenantId   The tenant ID for configuration updates
     * @param roomTypeId The room type ID (required only for ROOM image type)
     * @return The CloudFront URL of the uploaded file
     * @throws IOException if there's an error uploading the file
     */
    String uploadFile(MultipartFile file, ImageType imageType, Long tenantId, Long roomTypeId) throws IOException;
}
