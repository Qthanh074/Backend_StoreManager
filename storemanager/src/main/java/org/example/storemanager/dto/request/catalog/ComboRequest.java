package org.example.storemanager.dto.request.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ComboRequest {

    @NotBlank(message = "Mã combo không được để trống")
    @Size(max = 50, message = "Mã combo tối đa 50 ký tự")
    private String comboCode;

    @NotBlank(message = "Tên combo không được để trống")
    @Size(max = 150, message = "Tên combo tối đa 150 ký tự")
    private String comboName;

    @NotNull(message = "Giá combo không được để trống")
    @Positive(message = "Giá combo phải lớn hơn 0")
    private BigDecimal price;

    private Boolean isActive = true;

    private List<ComboDetailRequest> details;

    @Data
    public static class ComboDetailRequest {
        @NotNull(message = "ID sản phẩm không được để trống")
        private Long productId;

        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải lớn hơn 0")
        private BigDecimal quantity;
    }
}
