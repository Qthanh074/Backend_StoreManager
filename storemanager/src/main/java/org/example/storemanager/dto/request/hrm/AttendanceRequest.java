package org.example.storemanager.dto.request.hrm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

import org.example.storemanager.enums.hrm.AttendanceStatus;

@Data
public class AttendanceRequest {

    @NotNull(message = "ID nhân viên không được để trống")
    private Long userId;

    @NotNull(message = "Ngày làm việc không được để trống")
    private LocalDate workDate;

    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    private String gpsLocation;
    private AttendanceStatus status;
}
