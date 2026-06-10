package org.example.storemanager.dto.response.hrm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.storemanager.enums.hrm.UserStatus;
import org.example.storemanager.enums.hrm.EmploymentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String userCode;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private UserStatus status;

    private Long branchId;
    private String branchName;

    private Long roleId;
    private String roleName;

    private Long departmentId;
    private String departmentName;

    private Long positionId;
    private String positionTitle;

    private Long managerId;
    private String managerName;

    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private EmploymentType employmentType;

    private String identityId;
    private String taxId;
    private String avatarUrl;
    private Boolean mfaEnabled;

    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
