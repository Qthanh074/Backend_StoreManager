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
public class SystemConfigResponse {

    private Long id;
    private String configKey;
    private String value;

    private String category;
    private String dataType;

    private Boolean isEncrypted;
    private Boolean requiresRebootToApply;
    private String description;

    private LocalDateTime lastUpdatedAt;
    private String updatedByRole;
}
