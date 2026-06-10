package org.example.storemanager.dto.request.hrm;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {

    @NotBlank(message = "Mã phòng ban không được để trống")
    private String departmentCode;

    @NotBlank(message = "Tên phòng ban không được để trống")
    private String departmentName;

    private Long headUserId;
    private Long parentId;
    private String costCenterCode;
}
