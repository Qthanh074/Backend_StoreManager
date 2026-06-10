package org.example.storemanager.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductStatResponse {
    private Long productId;
    private String productName;
    private String productCode;
    private Integer quantitySold;
    private BigDecimal revenue;
}
