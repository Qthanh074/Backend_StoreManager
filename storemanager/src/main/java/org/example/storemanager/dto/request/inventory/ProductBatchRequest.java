package org.example.storemanager.dto.request.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductBatchRequest {

    @NotBlank(message = "Mã lô hàng không được để trống")
    private String batchNumber;

    private LocalDate manufactureDate;

    private LocalDate expiryDate;

    private String status; // ACTIVE, EXPIRED, ALMOST_EXPIRED

    @NotNull(message = "ID sản phẩm không được để trống")
    private Long productId;

    private BigDecimal initialUnits;

    private BigDecimal remainingUnits;

    private BigDecimal unitCost;

    private String supplierName;

    private String location;

    private String qualityStatus;

    private String inspector;

    private String notes;
}
