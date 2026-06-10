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
public class PaymentVoucherRequest {

    @NotBlank(message = "Mã phiếu chi không được để trống")
    private String voucherCode;

    @NotNull(message = "Ngày chi không được để trống")
    private LocalDateTime voucherDate;

    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    @NotBlank(message = "Người nhận không được để trống")
    private String receiverName;

    private VoucherStatus status = VoucherStatus.DRAFT;

    @NotNull(message = "Lý do giao dịch không được để trống")
    private Long reasonId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private String referenceDoc;
    private String receiverContact;
    private String payingAccount;
    private PaymentMethod paymentMethod;
    private String approver;
}
