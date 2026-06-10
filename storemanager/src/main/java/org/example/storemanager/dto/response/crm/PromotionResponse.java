package org.example.storemanager.dto.response.crm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.storemanager.enums.crm.PromotionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {

    private Long id;
    private String promoCode;
    private String promoName;

    private PromotionType type;
    private BigDecimal value;
    private BigDecimal minOrderAmount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean isActive;
}
