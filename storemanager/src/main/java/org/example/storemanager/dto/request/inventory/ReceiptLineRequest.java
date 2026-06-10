package org.example.storemanager.dto.request.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceiptLineRequest {
    @NotNull
    private Long productId;
    private Long productUnitId;
    private String productName;
    @NotNull
    private Integer quantity;
    @NotNull
    private BigDecimal unitCost;
    private BigDecimal lineTotal;
    private String batchNumber;
    private String expiryDate;
}
