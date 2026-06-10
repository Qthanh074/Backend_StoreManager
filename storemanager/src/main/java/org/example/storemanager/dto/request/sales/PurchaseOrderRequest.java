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
import org.example.storemanager.enums.sales.PaymentStatus;

@Data
public class PurchaseOrderRequest {

    @NotBlank(message = "Mã đơn nhập không được để trống")
    private String orderCode;

    @NotNull(message = "Ngày đặt hàng không được để trống")
    private LocalDateTime orderDate;

    private LocalDateTime expectedDelivery;

    @NotNull(message = "ID nhà cung cấp không được để trống")
    private Long supplierId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private OrderStatus status = OrderStatus.PENDING;
    private PaymentStatus paymentStatus;

    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    @NotEmpty(message = "Đơn nhập phải có ít nhất 1 sản phẩm")
    @Valid
    private List<PurchaseLineRequest> orderLines;
}
