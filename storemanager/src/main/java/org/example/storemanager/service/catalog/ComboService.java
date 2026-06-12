package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.ComboRequest;
import org.example.storemanager.dto.response.catalog.ComboResponse;
import org.springframework.data.domain.Page;

public interface ComboService {
    Page<ComboResponse> searchCombos(String search, Boolean isActive, int page, int size);
    ComboResponse getComboById(Long id);
    ComboResponse createCombo(ComboRequest request);
    ComboResponse updateCombo(Long id, ComboRequest request);
    void deleteCombo(Long id);
    java.util.List<ComboResponse> getSoftDeletedCombos();
}
