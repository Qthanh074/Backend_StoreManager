package org.example.storemanager.dto.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.storemanager.enums.finance.VoucherStatus;
import org.example.storemanager.enums.finance.PaymentMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptVoucherResponse {

    private Long id;
    private String voucherCode;
    private LocalDateTime voucherDate;
    private BigDecimal amount;

    private String payerName;
    private String payerContact;

    private VoucherStatus status;

    private Long reasonId;
    private String reasonName;

    private Long branchId;
    private String branchName;

    private String referenceDoc;
    private String receivingAccount;
    private PaymentMethod paymentMethod;

    private String createdBy;
    private LocalDateTime createdAt;
}
