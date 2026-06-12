package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.storemanager.enums.inventory.ReturnToSupplierStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnToSupplierResponse {

    private Long id;
    private String returnCode;
    private LocalDateTime returnDate;
    private String grnRefNumber;
    private BigDecimal totalAmount;
    private ReturnToSupplierStatus status;
    private String reason;

    private Long supplierId;
    private String supplierName;

    private Long branchId;
    private String branchName;

    private List<ReturnLineResponse> returnLines;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String note;
}
