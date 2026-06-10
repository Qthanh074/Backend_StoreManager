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
import org.example.storemanager.enums.sales.OrderOrigin;
import org.example.storemanager.enums.sales.PaymentStatus;
import org.example.storemanager.enums.finance.PaymentMethod;

@Data
public class SaleOrderRequest {

    @NotBlank(message = "Mã đơn hàng không được để trống")
    private String orderCode;

    @NotNull(message = "Ngày tạo đơn không được để trống")
    private LocalDateTime orderDate;

    private LocalDateTime expectedDelivery;

    @NotNull(message = "ID khách hàng không được để trống")
    private Long customerId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private OrderStatus status = OrderStatus.PENDING;
    private OrderOrigin origin;
    private String currency = "VND";

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    private Long posSessionId;
    private BigDecimal amountTendered;
    private BigDecimal changeAmount;

    @NotEmpty(message = "Đơn hàng phải có ít nhất 1 sản phẩm")
    @Valid
    private List<OrderLineRequest> orderLines;
}
