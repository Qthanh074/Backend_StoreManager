package org.example.storemanager.dto.response.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.sales.QuoteStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {

    private Long id;
    private String quoteCode;
    private LocalDateTime quoteDate;
    private LocalDateTime validUntil;

    private Long customerId;
    private String customerName;

    private Long branchId;
    private String branchName;

    private QuoteStatus status;

    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private List<QuoteLineResponse> quoteLines;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
