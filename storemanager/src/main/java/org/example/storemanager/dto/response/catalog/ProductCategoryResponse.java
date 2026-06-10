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
}
