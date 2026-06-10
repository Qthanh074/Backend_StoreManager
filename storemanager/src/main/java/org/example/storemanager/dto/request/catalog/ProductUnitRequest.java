package org.example.storemanager.dto.request.catalog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUnitRequest {

    @NotNull(message = "ID Đơn vị không được để trống")
    private Long unitId;

    @NotNull(message = "Tỷ lệ quy đổi không được để trống")
    @Positive(message = "Tỷ lệ quy đổi phải lớn hơn 0")
    private Integer conversionRate;

    private BigDecimal price;
    private String barcode;
}
