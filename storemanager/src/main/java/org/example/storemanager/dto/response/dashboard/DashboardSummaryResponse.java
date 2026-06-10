package org.example.storemanager.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;
    private Integer todayOrders;
    private Integer totalProducts;
    private Integer lowStockProductsCount;
    private BigDecimal outstandingDebt;

    private List<DailyRevenueStatResponse> revenueChart;
    private List<TopProductStatResponse> topProducts;
    private List<BranchRevenueStatResponse> branchStats;
}
