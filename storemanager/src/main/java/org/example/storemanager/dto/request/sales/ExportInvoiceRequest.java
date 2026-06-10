package org.example.storemanager.dto.request.sales;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.sales.OrderStatus;
import org.example.storemanager.enums.finance.PaymentMethod;

@Data
public class ExportInvoiceRequest {

    @NotBlank(message = "Mã hóa đơn không được để trống")
    private String invoiceCode;

    @NotNull(message = "Ngày lập không được để trống")
    private LocalDateTime invoiceDate;

    private Long customerId;
    private Long saleOrderId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private Long posSessionId;
    private PaymentMethod paymentMethod;
    private OrderStatus status = OrderStatus.DRAFT;

    private BigDecimal subTotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal totalAmount;

    @NotEmpty(message = "Hóa đơn phải có ít nhất 1 dòng")
    @Valid
    private List<InvoiceLineRequest> invoiceLines;
}
