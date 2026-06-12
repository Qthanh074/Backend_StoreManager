package org.example.storemanager.dto.response.wms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductLocationResponse {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private Long binId;
    private String binCode;
    private String zoneCode;
    private BigDecimal quantity;
}
