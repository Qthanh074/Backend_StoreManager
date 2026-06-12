package org.example.storemanager.dto.request.wms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehouseBinRequest {

    @NotBlank(message = "Mã ô/kệ không được để trống")
    private String binCode;

    private String barcode;         // Mã vạch dán trên kệ

    private BigDecimal maxCapacity; // Sức chứa tối đa

    @NotNull(message = "ID khu vực không được để trống")
    private Long zoneId;
}
