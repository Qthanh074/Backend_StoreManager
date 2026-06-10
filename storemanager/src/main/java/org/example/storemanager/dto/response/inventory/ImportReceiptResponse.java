package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.inventory.ReceiptStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceiptResponse {

    private Long id;
    private String receiptCode;
    private LocalDateTime receiptDate;

    private Long supplierId;
    private String supplierName;

    private Long branchId;
    private String branchName;

    private Long purchaseOrderId;
    private String purchaseOrderCode;

    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal tax;

    private ReceiptStatus status;

    private List<ReceiptLineResponse> receiptLines;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
