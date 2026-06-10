package org.example.storemanager.dto.response.omnichannel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookLogResponse {

    private Long id;
    private String eventType;
    private String payload;
    private Integer responseStatus;
    private String status;

    private LocalDateTime receivedAt;
    private Integer retryCount;
}
