package org.example.storemanager.dto.response.hrm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.example.storemanager.enums.hrm.LeaveStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponse {

    private Long id;
    private Long userId;
    private String userName;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDays;

    private String leaveType;
    private String reason;
    private LeaveStatus status;

    private String approvedBy;
    private String approvedByName;
}
