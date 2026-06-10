package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptLineResponse {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private Long productUnitId;
    private String unitName;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;
    private String batchNumber;
    private String expiryDate;
}
