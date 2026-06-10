package org.example.storemanager.dto.request.hrm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

import org.example.storemanager.enums.hrm.LeaveStatus;

@Data
public class LeaveRequestRequest {

    @NotNull(message = "ID nhân viên không được để trống")
    private Long userId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @NotBlank(message = "Loại nghỉ phép không được để trống")
    private String leaveType;

    private String reason;
    private LeaveStatus status = LeaveStatus.PENDING;
}
