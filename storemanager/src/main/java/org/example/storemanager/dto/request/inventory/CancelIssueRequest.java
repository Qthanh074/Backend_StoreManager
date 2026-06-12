package org.example.storemanager.dto.request.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.storemanager.enums.inventory.CancelIssueStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CancelIssueRequest {

    @NotBlank(message = "Mã phiếu xuất hủy không được để trống")
    private String cancelCode;

    @NotNull(message = "Ngày xuất hủy không được để trống")
    private LocalDateTime cancelDate;

    private BigDecimal totalValue;

    private String reason;

    @NotNull(message = "Trạng thái không được để trống")
    private CancelIssueStatus status = CancelIssueStatus.PENDING_APPROVAL;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private String note;

    @NotEmpty(message = "Phiếu xuất hủy phải có ít nhất 1 sản phẩm")
    @Valid
    private List<CancelLineRequest> cancelLines;
}
