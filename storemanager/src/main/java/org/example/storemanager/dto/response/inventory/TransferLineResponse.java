package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferLineResponse {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private Long productUnitId;
    private String unitName;
    private Integer transferQuantity;
    private String reason;
}
