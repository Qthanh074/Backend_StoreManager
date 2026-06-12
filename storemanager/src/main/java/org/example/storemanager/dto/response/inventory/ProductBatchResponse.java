package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchResponse {

    private Long id;
    private String batchNumber;
    private String sku;
    private String productName;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private BigDecimal initialUnits;
    private BigDecimal remainingUnits;
    private BigDecimal unitCost;
    private String supplierName;
    private String location;
    private String qualityStatus;
    private String inspector;
    private String status;
    private String notes;
}
