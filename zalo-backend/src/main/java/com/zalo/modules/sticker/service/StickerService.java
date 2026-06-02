package com.zalo.modules.sticker.service;

import com.zalo.common.configuration.json.G;
import com.zalo.common.service.ApiService;
import com.zalo.modules.sticker.entity.Sticker;
import com.zalo.modules.sticker.entity.StickerItem;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StickerService {
    ApiService apiService;
    StickerRepository stickerRepository;
    static String PACKAGE_URL = "https://stickers.zaloapp.com/sticker";
    static String ITEM_URL = "https://stickers.zaloapp.com/cate-stickers?cid=";

    public List<Sticker> getAll() {
        return stickerRepository.findAllByStt(1);
    }

    static String getSprite(String eid) {
        return "https://zalo-api.zadn.vn/api/emoticon/sprite?eid=" + eid + "&size=130";
    }

    public List<Sticker> syncZaloStickers() throws IOException {
        List<Sticker> allStickers = stickerRepository.findAllByStt(1);
        Map<String, Sticker> stickerMap = allStickers.stream()
                .collect(Collectors.toMap(
                        Sticker::getStickerId,
                        Function.identity()
                ));

        // 1. Gọi hàm get nhận về Object tổng
        Object rawResponse = apiService.get(PACKAGE_URL);

        // 2. Ép kiểu Object tổng về Map để bắt đầu lấy data theo Key
        Map<String, Object> responseMap = (Map<String, Object>) rawResponse;

        Map<String, Object> valueMap = (Map<String, Object>) responseMap.get("value");

        List<Sticker> stickers = new ArrayList<>();

        if (valueMap != null) {
            // 4. Lấy mảng "all" (Jackson sẽ tự động map mảng JSON thành java.util.List)
            List<Map<String, Object>> allList = (List<Map<String, Object>>) valueMap.get("all");

            if (allList != null) {
                for (Map<String, Object> pack : allList) {
                    String cid = (String) pack.get("id");
                    String name = (String) pack.get("name");
                    String iconUrl = (String) pack.get("iconUrl");
                    String thumbImg = (String) pack.get("thumbImg");

                    Sticker sticker = stickerMap.get(cid);

                    // Đã tồn tại -> update
                    if (sticker != null) {
                        sticker.setName(name);
                        sticker.setIconUrl(iconUrl);
                        sticker.setThumbImg(thumbImg);
                        sticker.setStt(1);
                        sticker.setItems(getZaloStickerItems(cid));

                        stickers.add(stickerRepository.save(sticker));
                        System.out.println("Sticker: " + G.toJson(sticker) + " size: " + stickers.size());
                    } else {
                        // Chưa tồn tại -> tạo mới
                        Sticker newSticker = new Sticker();

                        newSticker.setStickerId(cid);
                        newSticker.setName(name);
                        newSticker.setIconUrl(iconUrl);
                        newSticker.setThumbImg(thumbImg);
                        newSticker.setStt(1);
                        newSticker.setItems(getZaloStickerItems(cid));

                        stickers.add(stickerRepository.save(newSticker));
//                        System.out.println("Sticker: " + G.toJson(newSticker) + " size: " + stickers.size());
                    }
                }
            }
        }
        System.out.println("length: " + stickers.size());

        return stickers;
    }

    public List<StickerItem> getZaloStickerItems(String cid) throws IOException {

        Object rawResponse = apiService.get(ITEM_URL + cid);

        Map<String, Object> responseMap =
                (Map<String, Object>) rawResponse;

        List<Map<String, Object>> items =
                (List<Map<String, Object>>) responseMap.get("value");

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

                if (eid == null) {
                    continue;
                }

                String spriteUrl = getSprite(eid);
//                System.out.println("spriteUrl: " + spriteUrl);
                URL sprite = new URL(spriteUrl);

                HttpURLConnection conn =
                        (HttpURLConnection) sprite.openConnection();

                conn.setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0"
                );

                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedImage image;

                try (InputStream is = conn.getInputStream()) {
                    image = ImageIO.read(is);
                }

                if (image == null) {
                    continue;
                }

                int frameCount = image.getWidth() / image.getHeight();

                StickerItem stickerItem = new StickerItem();

                stickerItem.setId(id);
                stickerItem.setUrl(spriteUrl);
                stickerItem.setFrameCount(frameCount);

                stickerItems.add(stickerItem);

            } catch (Exception e) {
                System.out.println(
                        "Cannot read sprite: " + item
                );
            }
        }

        return stickerItems;
    }
}
