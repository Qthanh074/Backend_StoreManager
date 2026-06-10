package org.example.storemanager.dto.request.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

import org.example.storemanager.enums.system.RoleStatus;

@Data
public class RoleRequest {

    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 50)
    private String roleName;

    @Size(max = 255)
    private String description;

    private List<Long> permissionIds;

    private RoleStatus status = RoleStatus.ACTIVE;
}
