package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.enums.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    /**
     * Uploads a file to S3 and updates the corresponding configuration in DynamoDB if needed.
     *
     * @param file      The file to upload
     * @param imageType The type of image (LOGO, BANNER, ROOM)
     * @param tenantId  The tenant ID for configuration updates
     * @return The CloudFront URL of the uploaded file
     * @throws IOException if there's an error uploading the file
     */
    String uploadImage(MultipartFile file, ImageType imageType, Long tenantId) throws IOException;

    /**
     * Uploads multiple room images to S3 and updates the room type configuration.
     *
     * @param files      The list of files to upload
     * @param tenantId   The tenant ID for configuration updates
     * @param roomTypeId The room type ID
     * @return List of CloudFront URLs of the uploaded files
     */
    List<String> uploadImages(List<MultipartFile> files, Long tenantId, Long roomTypeId);
}
