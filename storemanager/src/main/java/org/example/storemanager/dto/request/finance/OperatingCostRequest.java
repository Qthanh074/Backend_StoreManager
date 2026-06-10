package org.example.storemanager.dto.request.finance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OperatingCostRequest {

    @NotBlank(message = "Mã chi phí không được để trống")
    private String costCode;

    @NotBlank(message = "Tên chi phí không được để trống")
    private String costName;

    @NotBlank(message = "Loại chi phí không được để trống")
    private String category;

    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    @NotNull(message = "Ngày phát sinh không được để trống")
    private LocalDate incurredDate;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private String paymentStatus = "UNPAID";
    private String notes;
}
