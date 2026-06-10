package org.example.storemanager.dto.response.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.sales.ReturnStatus;
import org.example.storemanager.enums.finance.DebtStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReturnResponse {

    private Long id;
    private String returnCode;
    private LocalDateTime returnDate;

    private Long customerId;
    private String customerName;

    private Long saleOrderId;
    private String saleOrderCode;

    private Long branchId;
    private String branchName;

    private ReturnStatus status;
    private DebtStatus refundStatus;

    private BigDecimal subTotal;
    private BigDecimal refundFee;
    private BigDecimal totalRefundAmount;

    private List<ReturnLineResponse> returnLines;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
