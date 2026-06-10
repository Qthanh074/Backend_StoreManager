package org.example.storemanager.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.example.storemanager.enums.inventory.TransferStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferResponse {

    private Long id;
    private String transferCode;
    private LocalDateTime transferDate;

    private Long fromBranchId;
    private String fromBranchName;

    private Long toBranchId;
    private String toBranchName;

    private TransferStatus status;

    private List<TransferLineResponse> transferLines;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
