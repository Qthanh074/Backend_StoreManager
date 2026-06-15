package org.example.storemanager.dto.request.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private String email;
    private String phone;

    @NotNull(message = "Phải chọn vai trò (Role) cho người dùng")
    private Long roleId;
    private Long branchId;
}
