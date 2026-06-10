package org.example.storemanager.dto.request.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemConfigRequest {

    @NotBlank(message = "Khóa cấu hình không được để trống")
    private String configKey;

    @NotBlank(message = "Giá trị cấu hình không được để trống")
    private String value;
}
