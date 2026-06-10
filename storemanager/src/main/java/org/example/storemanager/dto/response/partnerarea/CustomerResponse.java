package org.example.storemanager.dto.response.partnerarea;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String customerCode;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate dob;
    private String taxCode;
    private Boolean isActive;
    private Long groupId;
    private String groupName;
    private Long areaId;
    private String areaName;

    private Integer loyaltyPoints;
    private String loyaltyTierName;
    private BigDecimal totalRevenue;
}
