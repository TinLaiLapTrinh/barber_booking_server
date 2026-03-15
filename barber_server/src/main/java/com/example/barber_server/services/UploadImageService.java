package com.example.barber_server.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class UploadImageService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        return uploadResult.get("url").toString();
    }

    public Map<?, ?> deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) return null;
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public String extractPublicIdFromUrl(String url) {
        if (url == null || !url.contains("upload/")) return null;
        try {
            String parts[] = url.split("upload/");
            String pathAfterUpload = parts[1];
            String idWithExtension = pathAfterUpload.substring(pathAfterUpload.indexOf("/") + 1);
            return idWithExtension.substring(0, idWithExtension.lastIndexOf("."));
        } catch (Exception e) {
            return null;
        }
    }


}
