package com.zalo.modules.app.media;

import com.zalo.modules.app.media.dtos.requests.MinioPresignedRequest;
import com.zalo.modules.app.media.dtos.requests.PresignedRequest;
import com.zalo.modules.app.media.service.MediaService;
import com.zalo.modules.app.media.service.MinioService;
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
    private final MinioService minioService;

    @GetMapping("/presigned")
    public Map<String, Object> getPresignedUrl(@RequestParam String folder, @RequestParam String resourceType) { // "image", "video", hoặc "raw"
        Map<String, Object> response = new HashMap<>();
        response.put("apiKey", mediaService.cloudinary.config.apiKey);
        response.put("cloudName", mediaService.cloudinary.config.cloudName);

        response.put("presignedUrl", mediaService.generateSignature(folder, resourceType));
        return response;
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

    @GetMapping("/minio-presigned-url")
    public Map<String, Object> getMinioPresignedUrl(@ModelAttribute MinioPresignedRequest req) { // "image", "video", hoặc "raw"
        Map<String, Object> response = new HashMap<>();

        response.put("presignedUrl", minioService.generatePresignedUrl(req));
        return response;
    }

    @PostMapping("/minio-presigned-urls")
    public Map<String, Object> getMinioPresignedUrls(@RequestBody List<MinioPresignedRequest> req) {
        Map<String, Object> response = new HashMap<>();

        List<Object> presignedUrls = req.stream()
                .map(minioService::generatePresignedUrl)
                .collect(Collectors.toList());

        response.put("presignedUrls", presignedUrls);
        return response;
    }
}
