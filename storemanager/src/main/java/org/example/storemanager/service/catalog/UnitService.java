package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.UnitRequest;
import org.example.storemanager.dto.response.catalog.UnitResponse;

import java.util.List;

public interface UnitService {
    List<UnitResponse> getAllUnits();
    UnitResponse getUnitById(Long id);
    UnitResponse createUnit(UnitRequest request);
    UnitResponse updateUnit(Long id, UnitRequest request);
    void deleteUnit(Long id);
    List<UnitResponse> getSoftDeletedUnits();
}
