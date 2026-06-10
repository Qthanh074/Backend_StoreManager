package org.example.storemanager.dto.response.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.storemanager.enums.system.PosSessionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosSessionResponse {

    private Long id;
    private Long userId;
    private String userName;

    private Long branchId;
    private String branchName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BigDecimal openingCash;
    private BigDecimal expectedClosingCash;
    private BigDecimal actualClosingCash;
    private BigDecimal cashDifference;

    private BigDecimal totalRevenue;
    private Integer orderCount;

    private PosSessionStatus status;
}
