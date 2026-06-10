package org.example.storemanager.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchRevenueStatResponse {
    private Long branchId;
    private String branchName;
    private BigDecimal revenue;
    private Integer orders;
}
