package org.example.storemanager.dto.request.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PosSessionOpenRequest {

    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    @NotNull(message = "Tiền mặt đầu ca không được để trống")
    private BigDecimal openingCash;
}
