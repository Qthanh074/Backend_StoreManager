package org.example.storemanager.dto.request.sales;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.sales.ReturnStatus;
import org.example.storemanager.enums.finance.DebtStatus;

@Data
public class CustomerReturnRequest {

    @NotBlank(message = "Mã phiếu trả không được để trống")
    private String returnCode;

    @NotNull(message = "Ngày trả hàng không được để trống")
    private LocalDateTime returnDate;

    @NotNull(message = "ID khách hàng không được để trống")
    private Long customerId;

    private Long saleOrderId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private ReturnStatus status = ReturnStatus.PENDING;
    private DebtStatus refundStatus;

    private BigDecimal subTotal;
    private BigDecimal refundFee;
    private BigDecimal totalRefundAmount;

    @NotEmpty(message = "Phiếu trả hàng phải có ít nhất 1 sản phẩm")
    @Valid
    private List<ReturnLineRequest> returnLines;
}
