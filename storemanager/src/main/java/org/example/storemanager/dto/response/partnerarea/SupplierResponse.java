package org.example.storemanager.dto.response.partnerarea;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {

    private Long id;
    private String supplierCode;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private Boolean isActive;
    private Long groupId;
    private String groupName;
    private Long areaId;
    private String areaName;

    private BigDecimal totalPurchaseAmount;
}
