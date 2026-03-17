package com.zalo.service;

import com.cloudinary.Cloudinary;
import com.zalo.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public File uploadFile(MultipartFile file) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                Map.of("folder", "zalo_java_ionic")
        );

        File avatar = new File();
        avatar.setUrl(uploadResult.get("secure_url").toString());
        avatar.setPublicId(uploadResult.get("public_id").toString());

        return avatar;
    }

    public boolean deleteFile(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(
                    publicId,
                    Map.of() // có thể thêm option nếu cần
            );

            String status = result.get("result").toString();

            return "ok".equals(status) || "not found".equals(status);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
