package org.example.storemanager.dto.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.example.storemanager.enums.finance.DebtStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtLedgerResponse {

    private Long id;
    private Long entityId;
    private String entityName;
    private String entityType;

    private BigDecimal totalDebt;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    private LocalDate dueDate;
    private DebtStatus status;

    private String lastUpdatedBy;
}
