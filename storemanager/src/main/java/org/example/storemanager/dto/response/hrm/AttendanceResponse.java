package org.example.storemanager.dto.response.hrm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.example.storemanager.enums.hrm.AttendanceStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private Long id;
    private Long userId;
    private String userName;

    private LocalDate workDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private BigDecimal workingHours;

    private String gpsLocation;
    private AttendanceStatus status;
}
