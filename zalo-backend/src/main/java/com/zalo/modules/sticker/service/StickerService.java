package com.zalo.modules.sticker.service;

import com.zalo.common.service.ApiService;
import com.zalo.common.service.WebsocketService;
import com.zalo.modules.conversation.dto.respone.ConversationResponse;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.conversation.entities.ConversationType;
import com.zalo.modules.conversation.service.ConversationMemberRepository;
import com.zalo.modules.conversation.service.ConversationRepository;
import com.zalo.modules.conversation.service.ConversationService;
import com.zalo.modules.conversation.service.MemberService;
import com.zalo.modules.media.dtos.requests.MediaRequest;
import com.zalo.modules.media.dtos.responses.MediaResponse;
import com.zalo.modules.media.entities.Media;
import com.zalo.modules.media.entities.MediaType;
import com.zalo.modules.media.service.MediaInterface;
import com.zalo.modules.message.dto.response.LinkPreviewResponse;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.message.entity.DeliveryStatus;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.message.entity.MessageStatus;
import com.zalo.modules.message.entity.MessageType;
import com.zalo.modules.message.service.MessageRepository;
import com.zalo.modules.message.service.MessageService;
import com.zalo.modules.message.service.MessageStatusRepository;
import com.zalo.modules.sticker.entity.Sticker;
import com.zalo.modules.sticker.entity.StickerItem;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StickerService {
    ApiService apiService;
    StickerRepository stickerRepository;
    WebsocketService websocketService;
    MessageRepository messageRepo;
    EntityManager em;
    MessageStatusRepository statusRepo;
    ConversationMemberRepository memberRepo;
    ConversationRepository conversationRepository;
    ConversationService conversationService;
    MemberService memberService;
    MessageService messageService;

    static String STORAGE_DIR = "uploads/stickers/";
    static String PACKAGE_URL = "https://stickers.zaloapp.com/sticker";
    static String ITEM_URL = "https://stickers.zaloapp.com/cate-stickers?cid=";

//    public StickerService(ApiService apiService, StickerRepository stickerRepository, WebsocketService websocketService, MessageRepository messageRepo) {
//        this.apiService = apiService;
//        this.stickerRepository = stickerRepository;
//        this.websocketService = websocketService;
//        this.messageRepo = messageRepo;
//
//        try {
//            Files.createDirectories(Paths.get(STORAGE_DIR));
//            System.out.println("--> Đã kiểm tra và khởi tạo thư mục gốc: " + STORAGE_DIR);
//        } catch (IOException e) {
//            System.err.println("Không thể khởi tạo thư mục lưu trữ gốc: " + e.getMessage());
//        }
//    }

    public List<Sticker> getAll() {
        return stickerRepository.findAllByStt(1);
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
            return noAccent.replaceAll("đ", "d")
                    .replaceAll("[^a-z0-9\\s-]", "") // Xóa ký tự đặc biệt
                    .replaceAll("\\s+", "-")         // Thay khoảng trắng bằng dấu gạch ngang
                    .replaceAll("-+", "-");          // Thu gọn nhiều dấu gạch ngang liên tiếp
        } catch (Exception e) {
            return "pack";
        }
    }

    public List<Sticker> syncZaloStickers() throws IOException {
        List<Sticker> allStickers = stickerRepository.findAllByStt(1);
        Map<String, Sticker> stickerMap = allStickers.stream()
                .collect(Collectors.toMap(
                        Sticker::getStickerId,
                        Function.identity()
                ));

        Object rawResponse = apiService.get(PACKAGE_URL);
        Map<String, Object> responseMap = (Map<String, Object>) rawResponse;
        Map<String, Object> valueMap = (Map<String, Object>) responseMap.get("value");

        List<Sticker> stickers = new ArrayList<>();

        if (valueMap != null) {
            List<Map<String, Object>> allList = (List<Map<String, Object>>) valueMap.get("all");

            if (allList != null) {
                try {
                    Files.createDirectories(Paths.get(STORAGE_DIR));
                    System.out.println("--> Đã kiểm tra và khởi tạo thư mục gốc: " + STORAGE_DIR);
                } catch (IOException e) {
                    System.err.println("Không thể khởi tạo thư mục lưu trữ gốc: " + e.getMessage());
                }

                for (Map<String, Object> pack : allList) {
                    String cid = (String) pack.get("id");
                    String name = (String) pack.get("name");
                    String iconUrl = (String) pack.get("iconUrl");

                    // 1. Tạo đường dẫn thư mục riêng cho Pack: uploads/stickers/ten-pack_cid/
                    String safePackName = makeSafeFileName(name) + "_" + cid;
                    String packDirPath = STORAGE_DIR + safePackName + "/";

                    // 2. Tạo đường dẫn thư mục items bên trong thư mục Pack: uploads/stickers/ten-pack_cid/items/
                    String itemsDirPath = packDirPath + "items/";

                    // Tạo các thư mục vật lý trên ổ cứng
                    try {
                        Files.createDirectories(Paths.get(itemsDirPath));
                    } catch (IOException e) {
                        System.err.println("Không thể tạo cấu trúc thư mục cho pack: " + name);
                        continue;
                    }

                    // 3. Tải icon của pack vào ngay trong thư mục của Pack đó
                    String localIconUrl = downloadImageToServer(iconUrl, packDirPath + "pack_icon.png");

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

                String eid = UriComponentsBuilder
                        .fromUriString(url)
                        .build()
                        .getQueryParams()
                        .getFirst("eid");

                if (eid == null) continue;

                String spriteUrl = getSprite(eid);

                // Tải ảnh vào thư mục items đích của Pack hiện tại thay vì dồn chung vào một chỗ
                String localSpriteUrl = downloadImageToServer(spriteUrl, itemsDirPath + "item_" + id + ".png");
                if (localSpriteUrl == null) continue;

                File localFile = new File(localSpriteUrl);
                BufferedImage image = ImageIO.read(localFile);
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

    /**
     * Hàm helper: Nhận vào đường dẫn đích đầy đủ (gồm cả thư mục và tên file) để tải và lưu ảnh
     */
    private String downloadImageToServer(String sourceUrl, String fullTargetFilePath) {
        if (sourceUrl == null || sourceUrl.isEmpty()) return null;

        try {
            // 1. Kiểm tra file đã tồn tại trên server chưa
            Path targetPath = Paths.get(fullTargetFilePath);
            if (Files.exists(targetPath)) {
                System.out.println("Ảnh đã tồn tại trên server, bỏ qua không tải lại: " + fullTargetFilePath);
                return targetPath.toString().replace("\\", "/");
            }

            // 2. Nếu chưa tồn tại thì tiến hành tải như bình thường
            sourceUrl = sourceUrl.replace("×", "%C3%97");
            URL url = new URL(sourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Giả lập thêm User-Agent trình duyệt để tránh bị chặn
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                // Đảm bảo thư mục cha của file tồn tại (đề phòng thư mục chứa ảnh chưa được tạo)
                if (targetPath.getParent() != null) {
                    Files.createDirectories(targetPath.getParent());
                }

                try (InputStream in = conn.getInputStream()) {
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }

                System.out.println("Tải ảnh thành công: " + fullTargetFilePath);
                return targetPath.toString().replace("\\", "/");
            } else {
                System.err.println("Zalo từ chối kết nối, HTTP Code: " + responseCode + " cho URL: " + sourceUrl);
            }
        } catch (Exception e) {
            System.err.println("Lỗi hệ thống khi tải ảnh: " + sourceUrl + " - " + e.getMessage());
        }
        return null;
    }

    public void sendSticker(Long conversationId, Long senderId, StickerItem stickerItem) {
        // 1. Lưu Message chính
        Message m = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .sticker(stickerItem)
                .contentType(MessageType.STICKER)
                .build();
        messageRepo.save(m);

        // 3. Đồng bộ Persistence Context (Flush/Refresh)
        em.flush();
        em.refresh(m);
        Message finalMsg = messageService.findByIdWithRelationShip(m.getId(), conversationId);

        // 4. Cập nhật Last Message cho Conversation
        Conversation conv = conversationRepository.findById(conversationId).orElseThrow();
        conv.setLastMessageId(finalMsg.getId());
        conversationRepository.save(conv);

        // 5. Tạo MessageStatus cho các thành viên
        List<ConversationMember> members = memberRepo.findByConversationId(conversationId);
        List<MessageStatus> statuses = members.stream().map(member ->
                MessageStatus.builder()
                        .messageId(finalMsg.getId())
                        .userId(member.getUserId())
                        .status(member.getUserId().equals(senderId) ? DeliveryStatus.DELIVERED : DeliveryStatus.SENT)
                        .build()
        ).collect(Collectors.toList());
        statusRepo.saveAll(statuses);

        // 6. Đánh dấu đã xem cho chính mình
        messageService.markRead(conversationId, senderId, finalMsg.getId());

        // 7. Bắn WebSocket
        ConversationResponse convRes = new ConversationResponse(conversationService.findByIdWithRelationShip(conversationId), "recipient", "lastMessage", "createdBy", "avatar");
        if (conv.getType() == ConversationType.GROUP) {
            convRes.setMembers(memberService.getMembers(conv.getId()));
        }

        MessageResponse messageResponse = new MessageResponse(finalMsg, "sender");

        websocketService.sendMessage(messageResponse, convRes, members);
    }
}