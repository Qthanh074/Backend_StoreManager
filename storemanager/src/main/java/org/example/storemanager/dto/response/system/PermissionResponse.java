package org.example.storemanager.dto.response.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {

    private Long id;
    private String permissionKey;
    private String permissionName;
    private String module;
    private String description;
}
