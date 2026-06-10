package org.example.storemanager.dto.response.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.sales.OrderStatus;
import org.example.storemanager.enums.sales.PaymentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {

    private Long id;
    private String orderCode;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDelivery;

    private Long supplierId;
    private String supplierName;

    private Long branchId;
    private String branchName;

    private OrderStatus status;
    private PaymentStatus paymentStatus;

    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private List<PurchaseLineResponse> orderLines;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
