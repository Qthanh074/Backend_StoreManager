package org.example.storemanager.dto.request.catalog;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnitRequest {

    @NotBlank(message = "Tên đơn vị không được để trống")
    private String unitName;

    private String abbreviation;
    private String description;
    private Boolean isActive = true;
}
