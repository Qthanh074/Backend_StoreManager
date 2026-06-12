package org.example.storemanager.dto.response.wms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseZoneResponse {
    private Long id;
    private String zoneCode;
    private String zoneName;
    private String conditions;
    private Long branchId;
    private String branchName;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
