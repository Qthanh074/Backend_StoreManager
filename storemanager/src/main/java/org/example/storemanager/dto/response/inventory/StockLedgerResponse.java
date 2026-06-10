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
public class StockLedgerResponse {

    private Long id;
    private Long productId;
    private String productCode;
    private String productName;

    private Long branchId;
    private String branchName;

    private String transactionType;
    private Integer quantityChange;
    private Integer runningBalance;

    private String referenceDocument;
    private String notes;

    private LocalDateTime transactionDate;
    private String createdBy;
}
