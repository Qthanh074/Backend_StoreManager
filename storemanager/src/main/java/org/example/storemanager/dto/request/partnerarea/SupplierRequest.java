package org.example.storemanager.dto.request.partnerarea;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequest {

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    @Size(max = 150)
    private String name;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Email(message = "Email không đúng định dạng")
    @Size(max = 100)
    private String email;

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String taxCode;

    private Boolean isActive = true;

    private Long groupId;

    private Long areaId;
}
