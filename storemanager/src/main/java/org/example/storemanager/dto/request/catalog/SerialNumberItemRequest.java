package org.example.storemanager.dto.request.catalog;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SerialNumberItemRequest {
    private String serialNumber;
    private String macAddress;
    private String imei1;
    private String imei2;
    private LocalDateTime warrantyExpiry;
}
