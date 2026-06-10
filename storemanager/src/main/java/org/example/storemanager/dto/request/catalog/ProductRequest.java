package org.example.storemanager.dto.request.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Mã SKU không được để trống")
    @Size(max = 50, message = "Mã SKU tối đa 50 ký tự")
    private String productCode;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 150, message = "Tên sản phẩm tối đa 150 ký tự")
    private String name;

    private String description;

    @NotNull(message = "Giá bán cơ sở không được để trống")
    @Positive(message = "Giá bán phải lớn hơn 0")
    private BigDecimal basePrice;

    private BigDecimal costPrice;

    @Size(max = 50, message = "Barcode tối đa 50 ký tự")
    private String barcode;

    private Boolean isActive = true;

    private Long categoryId;

    @NotNull(message = "Đơn vị tính cơ sở không được để trống")
    private Long baseUnitId;

    private String brand;

    private String mainImageUrl;
}
