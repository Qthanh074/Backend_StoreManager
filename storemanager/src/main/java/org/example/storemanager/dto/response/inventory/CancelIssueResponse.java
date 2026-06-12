package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.storemanager.enums.inventory.CancelIssueStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelIssueResponse {

    private Long id;
    private String cancelCode;
    private LocalDateTime cancelDate;
    private BigDecimal totalValue;
    private String reason;
    private CancelIssueStatus status;

    private Long branchId;
    private String branchName;

    private List<CancelLineResponse> cancelLines;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String note;
}
