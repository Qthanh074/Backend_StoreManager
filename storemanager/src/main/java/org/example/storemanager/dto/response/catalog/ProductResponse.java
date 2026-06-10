package org.example.storemanager.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String productCode;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal costPrice;
    private String barcode;
    private Boolean isActive;
    private String brand;
    private String mainImageUrl;

    private Long categoryId;
    private String categoryName;

    private Long baseUnitId;
    private String baseUnitName;

    private Integer onHand;

    private List<ProductUnitResponse> units;

    private LocalDateTime updatedAt;
    private String updatedBy;
}
