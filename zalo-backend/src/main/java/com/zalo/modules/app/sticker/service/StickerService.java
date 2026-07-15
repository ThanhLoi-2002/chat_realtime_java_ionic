package com.zalo.modules.app.sticker.service;

import com.zalo.common.service.AiService;
import com.zalo.common.service.ApiService;
import com.zalo.modules.app.media.service.MinioService;
import com.zalo.modules.app.message.service.MessageService;
import com.zalo.modules.app.sticker.dto.request.GenerateSticker;
import com.zalo.modules.app.sticker.dto.request.UserStickerRequest;
import com.zalo.modules.app.sticker.entity.Sticker;
import com.zalo.modules.app.sticker.entity.StickerItem;
import com.zalo.modules.app.sticker.entity.UserSticker;
import io.minio.GetObjectArgs;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StickerService {
    ApiService apiService;
    AiService aiService;
    StickerRepository stickerRepository;
    MessageService messageService;
    MinioService minioService;
    UserStickerRepository userStickerRepository;

    static String STORAGE_DIR = "stickers/";
    static String PACKAGE_URL = "https://stickers.zaloapp.com/sticker";
    static String ITEM_URL = "https://stickers.zaloapp.com/cate-stickers?cid=";

    public List<Sticker> getAll() {
        return stickerRepository.findAllByStt(1);
    }

    public List<UserSticker> getUserAISticker(Long userId) {
        return userStickerRepository.findByCuAndStt(userId, 1);
    }

    static String getSprite(String eid) {
        return "https://zalo-api.zadn.vn/api/emoticon/sprite?eid=" + eid + "&size=130";
    }

    /**
     * Hàm helper: Biến đổi "Tên Tiếng Việt Có Dấu" thành "ten-tieng-viet-khong-dau" để làm tên thư mục an toàn
     */
    private String makeSafeFileName(String input) {
        if (input == null) return "unnamed";
        try {
            String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String noAccent = pattern.matcher(temp).replaceAll("").toLowerCase();
            return noAccent.replaceAll("đ", "d").replaceAll("[^a-z0-9\\s-]", "") // Xóa ký tự đặc biệt
                    .replaceAll("\\s+", "-")         // Thay khoảng trắng bằng dấu gạch ngang
                    .replaceAll("-+", "-");          // Thu gọn nhiều dấu gạch ngang liên tiếp
        } catch (Exception e) {
            return "pack";
        }
    }

    public List<Sticker> syncZaloStickers() throws IOException {
        List<Sticker> allStickers = stickerRepository.findAllByStt(1);
        Map<String, Sticker> stickerMap = allStickers.stream().collect(Collectors.toMap(Sticker::getStickerId, Function.identity()));

        Object rawResponse = apiService.get(PACKAGE_URL);
        Map<String, Object> responseMap = (Map<String, Object>) rawResponse;
        Map<String, Object> valueMap = (Map<String, Object>) responseMap.get("value");

        List<Sticker> stickers = new ArrayList<>();

        if (valueMap != null) {
            List<Map<String, Object>> allList = (List<Map<String, Object>>) valueMap.get("all");

            if (allList != null) {

                for (Map<String, Object> pack : allList) {
                    String cid = (String) pack.get("id");
                    String name = (String) pack.get("name");
                    String iconUrl = (String) pack.get("iconUrl");

                    // 1. Tạo đường dẫn thư mục riêng cho Pack: uploads/stickers/ten-pack_cid/
                    String safePackName = makeSafeFileName(name);
                    String packDirPath = STORAGE_DIR + "zalo/" + safePackName + "/";

                    // 2. Tạo đường dẫn thư mục items bên trong thư mục Pack: uploads/stickers/ten-pack_cid/items/
                    String itemsDirPath = packDirPath + "items/";

                    // 3. Tải icon của pack vào ngay trong thư mục của Pack đó
                    String localIconUrl = uploadImageToMinio(iconUrl, packDirPath + "icon.png");

                    Sticker sticker = stickerMap.get(cid);

                    if (sticker == null) {
                        sticker = new Sticker();
                        sticker.setStickerId(cid);
                    }

                    sticker.setName(name);
                    sticker.setIconUrl(localIconUrl != null ? "/" + localIconUrl : null);
                    sticker.setStt(1);
                    sticker.setSource("zalo");

                    // Truyền thêm đường dẫn thư mục items của từng pack vào hàm con
                    sticker.setItems(getZaloStickerItems(cid, itemsDirPath));

                    stickers.add(stickerRepository.save(sticker));
                }
            }
        }
        System.out.println("length: " + stickers.size());
        return stickers;
    }

    public List<StickerItem> getZaloStickerItems(String cid, String itemsDirPath) throws IOException {
        Object rawResponse = apiService.get(ITEM_URL + cid);
        Map<String, Object> responseMap = (Map<String, Object>) rawResponse;
        List<Map<String, Object>> items = (List<Map<String, Object>>) responseMap.get("value");

        List<StickerItem> stickerItems = new ArrayList<>();
        if (items == null) {
            return stickerItems;
        }

        for (Map<String, Object> item : items) {
            try {
                String url = (String) item.get("url");
                String id = (String) item.get("id");

                String eid = UriComponentsBuilder.fromUriString(url).build().getQueryParams().getFirst("eid");

                if (eid == null) continue;

                String spriteUrl = getSprite(eid);

                // Tải ảnh vào thư mục items đích của Pack hiện tại thay vì dồn chung vào một chỗ
                String localSpriteUrl = uploadImageToMinio(spriteUrl, itemsDirPath + id + ".png");
                if (localSpriteUrl == null) continue;

                BufferedImage image;

                try (
                        InputStream input =
                                minioService.minioClient.getObject(
                                        GetObjectArgs.builder()
                                                .bucket(minioService.bucket)
                                                .object(localSpriteUrl)
                                                .build()
                                )
                ) {

                    image = ImageIO.read(input);
                }

                if (image == null) continue;

                int frameCount = image.getWidth() / image.getHeight();

                StickerItem stickerItem = new StickerItem();
                stickerItem.setId(id);
                stickerItem.setStickerId(cid);

                // Lưu link sạch bắt đầu bằng dấu gạch chéo phục vụ Static Web handler (ví dụ: /uploads/stickers/abc_123/items/item_xyz.png)
                stickerItem.setUrl("/" + localSpriteUrl);
                stickerItem.setFrameCount(frameCount);

                stickerItems.add(stickerItem);

            } catch (Exception e) {
                System.err.println("Lỗi xử lý sprite item: " + item + " -> " + e.getMessage());
            }
        }
        return stickerItems;
    }

    private String uploadImageToMinio(String sourceUrl, String objectName) {
        if (sourceUrl == null || sourceUrl.isBlank()) return null;
        try {
            sourceUrl = sourceUrl.replace("×", "%C3%97");

            URL url = new URL(sourceUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {

                System.err.println("Zalo từ chối kết nối, HTTP Code: " + responseCode + " cho URL: " + sourceUrl);

                return null;
            }

            String contentType = Optional.ofNullable(conn.getContentType()).orElse("image/png");

            try (InputStream input = conn.getInputStream()) {

                minioService.upload(input, conn.getContentLengthLong(), objectName, contentType);
            }

            System.out.println("Upload MinIO thành công: " + objectName);

            return objectName;

        } catch (Exception e) {

            System.err.println("Lỗi upload MinIO: " + sourceUrl + " - " + e.getMessage());

            return null;
        }
    }

    public void sendSticker(Long conversationId, Long senderId, StickerItem stickerItem) {
        messageService.sendSticker(conversationId, senderId, stickerItem);
    }

    public ResponseEntity<byte[]> generateSticker(GenerateSticker req) {
        ResponseEntity<byte[]> response = aiService.postForBinary("/sticker/generate-spritesheet", req);

        // 2. Trích xuất các header cần thiết từ FastAPI
        String frameCount = response.getHeaders().getFirst("frame-count");

        // Trả về cho Frontend với đúng Header và Content-Type
        return ResponseEntity.ok()
//                .headers(response.getHeaders()) // Giữ nguyên các header: X-Frame-Count, X-Total-Width
                .header("frame-count", frameCount != null ? frameCount : "0")
                .contentType(MediaType.IMAGE_PNG)
                .body(response.getBody());
    }

    public UserSticker saveUserSticker(UserStickerRequest req, Long userId) {
        UserSticker userSticker = new UserSticker(req.getUrl(), req.getFrameCount());
        userSticker.setCu(userId);
        return userStickerRepository.save(userSticker);
    }
}