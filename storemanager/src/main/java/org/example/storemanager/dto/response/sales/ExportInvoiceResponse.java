package org.example.storemanager.dto.response.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.sales.OrderStatus;
import org.example.storemanager.enums.finance.PaymentMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportInvoiceResponse {

    private Long id;
    private String invoiceCode;
    private LocalDateTime invoiceDate;

    private Long customerId;
    private String customerName;

    private Long saleOrderId;
    private String saleOrderCode;

    private Long branchId;
    private String branchName;

    private Long posSessionId;
    private PaymentMethod paymentMethod;
    private OrderStatus status;

    private BigDecimal subTotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal totalAmount;

    private List<InvoiceLineResponse> invoiceLines;

    private String createdBy;
    private LocalDateTime createdAt;
}
