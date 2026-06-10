package org.example.storemanager.dto.request.crm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.storemanager.enums.crm.PromotionType;

@Data
public class PromotionRequest {

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    private String promoCode;

    @NotBlank(message = "Tên chương trình không được để trống")
    private String promoName;

    @NotNull(message = "Loại khuyến mãi không được để trống")
    private PromotionType type;

    @NotNull(message = "Giá trị khuyến mãi không được để trống")
    @Positive(message = "Giá trị phải lớn hơn 0")
    private BigDecimal value;

    private BigDecimal minOrderAmount;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
