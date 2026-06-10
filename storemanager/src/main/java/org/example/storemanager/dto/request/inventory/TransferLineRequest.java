package org.example.storemanager.dto.request.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferLineRequest {
    @NotNull
    private Long productId;
    private Long productUnitId;
    private String productName;
    @NotNull
    private Integer transferQuantity;
    private String reason;
}
