package com.zalo.modules.media;

import com.zalo.modules.media.dtos.requests.PresignedRequest;
import com.zalo.modules.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping("/presigned")
    public Map<String, Object> getPresignedUrl(@RequestParam String folder, @RequestParam String resourceType) { // "image", "video", hoặc "raw"

        return mediaService.generateSignature(folder, resourceType);
    }

    @PostMapping("/presigned-urls")
    public Map<String, Object> getMultiplePresignedUrls(@RequestBody List<PresignedRequest> requests) {
        Map<String, Object> response = new HashMap<>();
        response.put("apiKey", mediaService.cloudinary.config.apiKey);
        response.put("cloudName", mediaService.cloudinary.config.cloudName);

        List<Object> presignedUrls = requests.stream()
                .map(req -> mediaService.generateSignature(req.folder, req.getResourceType()))
                .collect(Collectors.toList());

        response.put("presignedUrls", presignedUrls);
        return response;
    }
}
