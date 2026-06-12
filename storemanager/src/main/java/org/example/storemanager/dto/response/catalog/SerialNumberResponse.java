package org.example.storemanager.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumberResponse {
    private Long id;
    private String serialNumber;
    private String status;
    private Long importReceiptId;
    private String macAddress;
    private String imei1;
    private String imei2;
    private java.time.LocalDateTime warrantyExpiry;
    private java.time.LocalDateTime deletedAt;
    private String deletedBy;
    private java.time.LocalDateTime createdAt;
    private String createdBy;
    private java.time.LocalDateTime updatedAt;
    private String updatedBy;
}
