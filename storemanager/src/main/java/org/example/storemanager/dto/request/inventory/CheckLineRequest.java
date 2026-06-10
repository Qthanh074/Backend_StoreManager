package org.example.storemanager.dto.request.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckLineRequest {
    @NotNull
    private Long productId;
    private Long productUnitId;
    private String productName;
    @NotNull
    private Integer expectedQuantity;
    @NotNull
    private Integer actualQuantity;
    private String reason;
}
