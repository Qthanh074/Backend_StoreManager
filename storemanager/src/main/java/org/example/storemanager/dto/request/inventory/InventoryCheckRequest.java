package org.example.storemanager.dto.request.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.inventory.CheckStatus;

@Data
public class InventoryCheckRequest {

    @NotBlank(message = "Mã phiếu kiểm không được để trống")
    private String checkCode;

    @NotNull(message = "Ngày kiểm kê không được để trống")
    private LocalDateTime checkDate;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private CheckStatus status = CheckStatus.DRAFT;

    @NotEmpty(message = "Phiếu kiểm kê phải có ít nhất 1 sản phẩm")
    @Valid
    private List<CheckLineRequest> checkLines;
}
