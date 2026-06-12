package org.example.storemanager.service.inventory;

import org.example.storemanager.dto.request.inventory.ProductBatchRequest;
import org.example.storemanager.dto.response.inventory.ProductBatchResponse;

import java.util.List;

public interface ProductBatchService {

    List<ProductBatchResponse> getAllBatches();

    List<ProductBatchResponse> getBatchesByProduct(Long productId);

    ProductBatchResponse getBatchById(Long id);

    ProductBatchResponse createBatch(ProductBatchRequest request);

    ProductBatchResponse updateBatch(Long id, ProductBatchRequest request);

    void deleteBatch(Long id);
}
