package org.example.storemanager.dto.response.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {

    private Long id;
    private Long userId;
    private String userName;

    private String actionType;
    private String moduleName;

    private String entityType;
    private Long entityId;
    private String entityLabel;

    private String description;
    private String ipAddress;
    private String userAgent;

    private String status;
    private LocalDateTime createdAt;
}
