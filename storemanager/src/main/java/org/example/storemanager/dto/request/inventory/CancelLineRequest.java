package org.example.storemanager.dto.request.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CancelLineRequest {

    @NotNull(message = "ID sản phẩm không được để trống")
    private Long productId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng xuất hủy phải lớn hơn hoặc bằng 1")
    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subTotal;
}
