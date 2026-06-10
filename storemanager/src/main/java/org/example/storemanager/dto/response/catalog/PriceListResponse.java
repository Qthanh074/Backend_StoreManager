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
}
