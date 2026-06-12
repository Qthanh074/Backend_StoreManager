package org.example.storemanager.dto.request.catalog;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductCategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    private String categoryName;

    private String description;
    private Long parentId;       // ID danh mục cha (nếu đã tồn tại)
    private String parentName;   // Tên danh mục cha (tự động tạo mới nếu parentId = null)
    private Boolean isActive = true;

    private String department;
    private String manager;
    private String inventoryGlCode;
    private String cogsGlCode;
    private org.example.storemanager.enums.catalog.TaxClass taxClass;
}
