package org.example.storemanager.dto.request.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.storemanager.enums.inventory.ReturnToSupplierStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReturnToSupplierRequest {

    @NotBlank(message = "Mã phiếu trả hàng không được để trống")
    private String returnCode;

    @NotNull(message = "Ngày trả hàng không được để trống")
    private LocalDateTime returnDate;

    private String grnRefNumber; // Mã chứng từ nhập kho đối chiếu

    private BigDecimal totalAmount;

    @NotNull(message = "Trạng thái không được để trống")
    private ReturnToSupplierStatus status = ReturnToSupplierStatus.PENDING_SUPPLIER_APPROVAL;

    private String reason;

    @NotNull(message = "ID nhà cung cấp không được để trống")
    private Long supplierId;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private String note;

    @NotEmpty(message = "Phiếu trả hàng phải có ít nhất 1 sản phẩm")
    @Valid
    private List<ReturnLineRequest> returnLines;
}
