package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.inventory.CheckStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckResponse {

    private Long id;
    private String checkCode;
    private LocalDateTime checkDate;

    private Long branchId;
    private String branchName;

    private CheckStatus status;

    private List<CheckLineResponse> checkLines;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
