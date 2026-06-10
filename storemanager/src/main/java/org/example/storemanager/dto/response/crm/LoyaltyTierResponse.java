package org.example.storemanager.dto.response.crm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyTierResponse {

    private Long id;
    private String tierName;
    private BigDecimal minSpendThreshold;
    private BigDecimal pointsMultiplier;
    private BigDecimal discountPercentage;
    private Boolean freeShippingEligible;

    private Integer activeMembersCount;
}
