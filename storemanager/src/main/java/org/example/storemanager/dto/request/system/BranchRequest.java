package org.example.storemanager.dto.request.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchRequest {

    @NotBlank(message = "Mã chi nhánh không được để trống")
    private String branchCode;

    @NotBlank(message = "Tên chi nhánh không được để trống")
    private String branchName;

    private String address;
    private String phone;
    private Boolean isActive = true;

    private Long managerId;
}
