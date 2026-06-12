package org.example.storemanager.dto.response.wms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseBinResponse {
    private Long id;
    private String binCode;
    private String barcode;
    private BigDecimal maxCapacity;
    private Long zoneId;
    private String zoneCode;
    private String zoneName;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
