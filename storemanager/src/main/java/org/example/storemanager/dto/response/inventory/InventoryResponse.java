package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private Long id;
    private Long productId;
    private String productCode;
    private String productName;

    private Long branchId;
    private String branchName;

    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer quantityAvailable;

    private String locationBin;

    private LocalDateTime lastUpdatedAt;
}
