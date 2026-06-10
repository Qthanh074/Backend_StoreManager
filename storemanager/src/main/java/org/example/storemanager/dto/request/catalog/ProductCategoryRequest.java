package org.example.storemanager.dto.request.catalog;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductCategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    private String categoryName;

    private String description;
    private Long parentId;
    private Boolean isActive = true;
}
