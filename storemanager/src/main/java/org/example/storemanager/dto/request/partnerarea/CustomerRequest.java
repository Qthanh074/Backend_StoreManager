package org.example.storemanager.dto.request.partnerarea;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequest {

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 150)
    private String name;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Email(message = "Email không đúng định dạng")
    @Size(max = 100)
    private String email;

    @Size(max = 255)
    private String address;

    private LocalDate dob;

    @Size(max = 50)
    private String taxCode;

    private Boolean isActive = true;

    private Long groupId;

    private Long areaId;
}
