package org.example.storemanager.dto.request.sales;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.sales.QuoteStatus;

@Data
public class QuoteRequest {

    @NotBlank(message = "Mã báo giá không được để trống")
    private String quoteCode;

    @NotNull(message = "Ngày báo giá không được để trống")
    private LocalDateTime quoteDate;

    private LocalDateTime validUntil;

    @NotNull(message = "ID khách hàng không được để trống")
    private Long customerId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private QuoteStatus status = QuoteStatus.DRAFT;

    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    @NotEmpty(message = "Báo giá phải có ít nhất 1 sản phẩm")
    @Valid
    private List<QuoteLineRequest> quoteLines;
}
