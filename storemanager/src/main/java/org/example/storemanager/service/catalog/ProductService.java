package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.ProductRequest;
import org.example.storemanager.dto.request.catalog.SerialNumberBatchRequest;
import org.example.storemanager.dto.response.catalog.ProductResponse;
import org.example.storemanager.dto.response.catalog.SerialNumberResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductService {
    Page<ProductResponse> searchProducts(String search, Long categoryId, Boolean isActive, int page, int size);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    ProductResponse uploadProductImage(Long id, MultipartFile file);
    List<SerialNumberResponse> getProductSerials(Long productId, String status);
    List<SerialNumberResponse> addProductSerials(Long productId, SerialNumberBatchRequest request);
    List<ProductResponse> getSoftDeletedProducts();
    List<SerialNumberResponse> getSoftDeletedSerials();
}
