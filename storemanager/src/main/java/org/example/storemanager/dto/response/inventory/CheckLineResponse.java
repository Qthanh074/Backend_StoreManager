package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckLineResponse {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private Long productUnitId;
    private String unitName;
    private Integer expectedQuantity;
    private Integer actualQuantity;
    private Integer discrepancy;
    private String reason;
}
