package org.example.storemanager.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitResponse {
    private Long id;
    private String unitName;
    private String abbreviation;
}
