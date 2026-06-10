package org.example.storemanager.dto.request.catalog;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PriceListRequest {

    @NotBlank(message = "Mã bảng giá không được để trống")
    private String listCode;

    @NotBlank(message = "Tên bảng giá không được để trống")
    private String listName;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long branchId;
    private Boolean isActive = true;
}
