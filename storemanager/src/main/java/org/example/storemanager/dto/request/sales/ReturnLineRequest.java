package org.example.storemanager.dto.request.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReturnLineRequest {
    @NotNull
    private Long productId;
    private Long productUnitId;
    private String productName;
    @NotNull
    private Integer returnQuantity;
    @NotNull
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String reason;
}
