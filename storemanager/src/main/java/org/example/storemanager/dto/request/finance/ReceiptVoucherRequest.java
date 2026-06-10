package org.example.storemanager.dto.request.finance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.storemanager.enums.finance.VoucherStatus;
import org.example.storemanager.enums.finance.PaymentMethod;

@Data
public class ReceiptVoucherRequest {

    @NotBlank(message = "Mã phiếu thu không được để trống")
    private String voucherCode;

    @NotNull(message = "Ngày thu không được để trống")
    private LocalDateTime voucherDate;

    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    @NotBlank(message = "Tên người nộp không được để trống")
    private String payerName;

    private VoucherStatus status = VoucherStatus.DRAFT;

    @NotNull(message = "Lý do giao dịch không được để trống")
    private Long reasonId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private String referenceDoc;
    private String payerContact;
    private String receivingAccount;
    private PaymentMethod paymentMethod;
}
