package com.zalo.modules.media.service;

import com.cloudinary.Cloudinary;
import com.zalo.common.entity.File;
import com.zalo.modules.media.dtos.requests.MediaRequest;
import com.zalo.modules.media.entities.Media;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaService implements MediaInterface {

    public final Cloudinary cloudinary;
    private final MediaRepository mediaRepository;

    public Map<String, Object> generateSignature(String folder, String resourceType) {
        long timestamp = System.currentTimeMillis() / 1000L;
        Map<String, Object> params = new HashMap<>();

        params.put("timestamp", timestamp + 300);// hạn 5 phút
        params.put("folder", folder);

        // --- Cấu hình nén chuyên sâu cho từng loại file ---
        if ("IMAGE".equals(resourceType)) {
            // q_auto:good: Cân bằng tốt nhất giữa độ nét và dung lượng (rõ hơn q_auto thường)
            // f_auto: Tự động chuyển sang WebP/AVIF để giữ chi tiết ảnh
            // c_limit,w_2560: Giới hạn độ phân giải 2K (giữ độ nét cực cao)
            params.put("eager", "c_limit,w_2560,h_2560/q_auto:good,f_auto");
        } else if ("VIDEO".equals(resourceType)) {
            // q_auto: Tối ưu chất lượng video tự động
            // br_auto: Tự động điều chỉnh Bitrate theo độ phức tạp của cảnh quay
            // c_limit,w_1920: Giữ chất lượng Full HD (1080p) rõ nét
            params.put("eager", "c_limit,w_1920,h_1080/q_auto,f_auto,br_auto");
            // Video nặng nên bắt buộc dùng async để không làm treo request
            params.put("eager_async", true);
        }

        // Tạo chữ ký bảo mật
        String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

        // Trả về cho Front-end
//        Map<String, Object> response = new HashMap<>(params);
        params.put("signature", signature);
//        response.put("api_key", cloudinary.config.apiKey);
//        response.put("cloud_name", cloudinary.config.cloudName);

        return params;
    }

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

    private Media toEntity(MediaRequest dto, Long userId) {
        Media m = new Media();
        m.setPublicId(dto.publicId);
        m.setSecureUrl(dto.secureUrl);
        m.setModuleId(dto.moduleId);
        m.setModuleType(dto.moduleType);
        m.setBytes(dto.bytes);
        m.setFormat(dto.format);
        m.setResourceType(dto.resourceType);
        m.setWidth(dto.width);
        m.setHeight(dto.height);
        m.setCu(userId);

        return m;
    }

    @Override
    public void save(MediaRequest dto, Long userId) {
        mediaRepository.save(toEntity(dto, userId));
    }

    @Override
    public void saveAll(List<MediaRequest> dtos, Long userId) {
        List<Media> list = dtos.stream().map(i -> toEntity(i, userId)).toList();

        mediaRepository.saveAll(list);
    }
}
