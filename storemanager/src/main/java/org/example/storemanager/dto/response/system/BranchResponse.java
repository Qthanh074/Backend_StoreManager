package org.example.storemanager.dto.response.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {

    private Long id;
    private String branchCode;
    private String branchName;
    private String address;
    private String phone;
    private Boolean isActive;

    private Long managerId;
    private String managerName;

    private Integer employeeCount;
}
