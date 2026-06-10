package org.example.storemanager.dto.request.crm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoyaltyTierRequest {

    @NotBlank(message = "Tên hạng không được để trống")
    private String tierName;

    @NotNull(message = "Ngưỡng chi tiêu không được để trống")
    @PositiveOrZero(message = "Ngưỡng chi tiêu không được âm")
    private BigDecimal minSpendThreshold;

    @NotNull(message = "Hệ số tích điểm không được để trống")
    @PositiveOrZero(message = "Hệ số không được âm")
    private BigDecimal pointsMultiplier;

    private BigDecimal discountPercentage;
    private Boolean freeShippingEligible = false;
}
