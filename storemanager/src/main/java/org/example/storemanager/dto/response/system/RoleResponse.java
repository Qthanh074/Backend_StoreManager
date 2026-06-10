package org.example.storemanager.dto.response.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.example.storemanager.enums.system.RoleStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String roleName;
    private String description;
    private RoleStatus status;

    private Integer assignedUsersCount;

    private List<PermissionResponse> permissions;
}
