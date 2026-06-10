package org.example.storemanager.dto.response.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.sales.OrderStatus;
import org.example.storemanager.enums.sales.OrderOrigin;
import org.example.storemanager.enums.sales.PaymentStatus;
import org.example.storemanager.enums.finance.PaymentMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderResponse {

    private Long id;
    private String orderCode;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDelivery;

    private Long customerId;
    private String customerName;
    private String customerPhone;

    private Long branchId;
    private String branchName;

    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private OrderOrigin origin;
    private String currency;

    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    private Long posSessionId;
    private BigDecimal amountTendered;
    private BigDecimal changeAmount;

    private List<OrderLineResponse> orderLines;
    private String itemsSummary;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
