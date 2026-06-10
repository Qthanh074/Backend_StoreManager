package org.example.storemanager.dto.request.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.inventory.ReceiptStatus;

@Data
public class ImportReceiptRequest {

    @NotBlank(message = "Mã phiếu nhập không được để trống")
    private String receiptCode;

    @NotNull(message = "Ngày nhập không được để trống")
    private LocalDateTime receiptDate;

    private Long supplierId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private Long purchaseOrderId;

    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal tax;

    private ReceiptStatus status = ReceiptStatus.PENDING;

    @NotEmpty(message = "Phiếu nhập phải có ít nhất 1 sản phẩm")
    @Valid
    private List<ReceiptLineRequest> receiptLines;
}
