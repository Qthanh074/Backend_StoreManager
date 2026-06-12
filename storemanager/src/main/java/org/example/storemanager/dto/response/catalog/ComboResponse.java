package org.example.storemanager.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComboResponse {

    private Long id;
    private String comboCode;
    private String comboName;
    private BigDecimal price;
    private Boolean isActive;
    private java.time.LocalDateTime deletedAt;
    private String deletedBy;
    private java.time.LocalDateTime createdAt;
    private String createdBy;
    private java.time.LocalDateTime updatedAt;
    private String updatedBy;
    private List<ComboDetailResponse> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComboDetailResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productCode;
        private BigDecimal quantity;
    }
}
