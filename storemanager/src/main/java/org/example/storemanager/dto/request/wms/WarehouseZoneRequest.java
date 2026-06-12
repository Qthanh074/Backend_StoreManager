package org.example.storemanager.dto.request.wms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehouseZoneRequest {

    @NotBlank(message = "Mã khu vực không được để trống")
    private String zoneCode;

    @NotBlank(message = "Tên khu vực không được để trống")
    private String zoneName;

    private String conditions; // Điều kiện lưu trữ: nhiệt độ, độ ẩm...

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;
}
