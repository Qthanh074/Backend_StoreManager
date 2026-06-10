package org.example.storemanager.dto.request.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.inventory.TransferStatus;

@Data
public class StockTransferRequest {

    @NotBlank(message = "Mã phiếu chuyển không được để trống")
    private String transferCode;

    @NotNull(message = "Ngày chuyển không được để trống")
    private LocalDateTime transferDate;

    @NotNull(message = "Chi nhánh xuất không được để trống")
    private Long fromBranchId;

    @NotNull(message = "Chi nhánh nhập không được để trống")
    private Long toBranchId;

    private TransferStatus status = TransferStatus.DRAFT;

    @NotEmpty(message = "Phiếu chuyển kho phải có ít nhất 1 sản phẩm")
    @Valid
    private List<TransferLineRequest> transferLines;
}
