package org.example.storemanager.dto.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatingCostResponse {

    private Long id;
    private String costCode;
    private String costName;
    private String category;

    private BigDecimal amount;
    private LocalDate incurredDate;

    private Long branchId;
    private String branchName;

    private String paymentStatus;
    private String notes;

    private String authorizedBy;
}
