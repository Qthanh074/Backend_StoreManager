package org.example.storemanager.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnitResponse {

    private Long id;
    private Long unitId;
    private String unitName;
    private String unitCode;
    private Integer conversionRate;
    private BigDecimal price;
    private String barcode;
}
