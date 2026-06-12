package org.example.storemanager.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponse {

    private Long id;
    private String categoryName;
    private String description;
    private Long parentId;
    private String parentName;
    private Boolean isActive;
    private Integer productCount;
    private java.util.List<ProductCategoryResponse> children;

    private String department;
    private String manager;
    private String inventoryGlCode;
    private String cogsGlCode;
    private org.example.storemanager.enums.catalog.TaxClass taxClass;
    private java.time.LocalDateTime deletedAt;
    private String deletedBy;
    private java.time.LocalDateTime createdAt;
    private String createdBy;
    private java.time.LocalDateTime updatedAt;
    private String updatedBy;
}
