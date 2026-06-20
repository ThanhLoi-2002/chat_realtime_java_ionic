package com.zalo.modules.app.media.dtos.responses;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinioUploadResponse {

    String objectName;

    Long size;

    String contentType;

    String etag;

    Instant lastModified;

    String url;
}
