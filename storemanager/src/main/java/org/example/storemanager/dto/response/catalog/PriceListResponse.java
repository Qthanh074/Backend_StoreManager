package org.example.storemanager.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceListResponse {

    private Long id;
    private String listCode;
    private String listName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Long branchId;
    private String branchName;
    private java.time.LocalDateTime deletedAt;
    private String deletedBy;
    private java.time.LocalDateTime createdAt;
    private String createdBy;
    private java.time.LocalDateTime updatedAt;
    private String updatedBy;
    private java.util.List<PriceListDetailResponse> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceListDetailResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productCode;
        private java.math.BigDecimal price;
    }
}
