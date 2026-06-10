package org.example.storemanager.dto.request.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PosSessionCloseRequest {

    @NotNull(message = "Tiền mặt thực tế không được để trống")
    private BigDecimal actualClosingCash;
}
