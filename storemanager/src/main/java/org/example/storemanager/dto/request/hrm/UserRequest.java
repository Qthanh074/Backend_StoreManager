package org.example.storemanager.dto.request.hrm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

import org.example.storemanager.enums.hrm.UserStatus;
import org.example.storemanager.enums.hrm.EmploymentType;

@Data
public class UserRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(max = 50)
    private String username;

    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @NotNull(message = "Trạng thái không được để trống")
    private UserStatus status;

    @NotNull(message = "ID chi nhánh không được để trống")
    private Long branchId;

    private Long roleId;

    private Long departmentId;
    private Long positionId;
    private Long managerId;

    private LocalDate dateOfBirth;
    private LocalDate hireDate;

    private EmploymentType employmentType;

    private String identityId;
    private String taxId;
    private String avatarUrl;
    private String notes;
    private Boolean mfaEnabled = false;
}
