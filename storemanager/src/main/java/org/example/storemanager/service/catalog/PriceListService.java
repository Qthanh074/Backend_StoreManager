package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.PriceListRequest;
import org.example.storemanager.dto.response.catalog.PriceListResponse;

import java.util.List;

public interface PriceListService {
    List<PriceListResponse> getActivePriceLists(Long branchId, Boolean isActive);
    PriceListResponse getPriceListById(Long id);
    PriceListResponse createPriceList(PriceListRequest request);
    PriceListResponse updatePriceList(Long id, PriceListRequest request);
    void deletePriceList(Long id);
    List<PriceListResponse> getSoftDeletedPriceLists();
}
