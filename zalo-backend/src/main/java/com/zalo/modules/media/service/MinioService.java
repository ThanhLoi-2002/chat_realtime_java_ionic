package com.zalo.modules.media.service;

import com.zalo.modules.media.dtos.responses.MinioUploadResponse;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioService {
    public final MinioClient minioClient;

    @Value("${minio.bucket}")
    public String bucket;

    @Value("${minio.endpoint}")
    public String endpoint;

    public MinioUploadResponse upload(
            InputStream inputStream,
            long size,
            String objectName,
            String contentType
    ) throws Exception {

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(inputStream, size, -1)
                        .contentType(contentType)
                        .build()
        );

        StatObjectResponse stat =
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .build()
                );

        return MinioUploadResponse.builder()
                .objectName(objectName)
                .size(stat.size())
                .contentType(stat.contentType())
                .etag(stat.etag())
                .lastModified(
                        stat.lastModified().toInstant()
                )
                .build();
    }


    /**
     * Tạo Presigned URL để Front-end có thể tự upload file trực tiếp lên Minio
     * @param folder Thư mục lưu trữ (vd: "avatars", "videos")
     * @param fileName Tên file (vd: "user_123.mp4")
     * @return Map chứa thông tin URL và metadata
     */
    public Map<String, Object> generatePresignedUrl(String folder, String fileName) {
        try {
            // Ghép folder và fileName thành object name trong Minio (vd: avatars/user_123.mp4)
            String objectName = folder + "/" + fileName;

            // Tạo Presigned URL với phương thức PUT, hết hạn sau 5 phút (300 giây)
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT) // Bắt buộc dùng PUT để upload
                            .bucket(bucket)
                            .object(objectName)
                            .expiry(5, TimeUnit.MINUTES) // Hạn 5 phút giống Cloudinary của bạn
                            .build()
            );

            // Trả về cho Front-end tương tự cấu trúc bạn muốn
            Map<String, Object> response = new HashMap<>();
            response.put("uploadUrl", url);
            response.put("objectName", objectName);
            response.put("bucket", bucket);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo Presigned URL: " + e.getMessage(), e);
        }
    }
}
