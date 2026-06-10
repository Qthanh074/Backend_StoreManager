package org.example.storemanager.dto.response.hrm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {

    private Long id;
    private String departmentCode;
    private String departmentName;

    private Long headUserId;
    private String headUserName;

    private Long parentId;
    private String parentName;

    private String costCenterCode;
    private Integer totalEmployees;
}
