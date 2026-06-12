package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.ProductCategoryRequest;
import org.example.storemanager.dto.response.catalog.ProductCategoryResponse;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryResponse> getAllCategories();
    List<ProductCategoryResponse> getRootCategories();
    List<ProductCategoryResponse> getChildrenOf(Long parentId);
    ProductCategoryResponse getCategoryById(Long id);
    ProductCategoryResponse createCategory(ProductCategoryRequest request);
    ProductCategoryResponse updateCategory(Long id, ProductCategoryRequest request);
    void deleteCategory(Long id);
    List<ProductCategoryResponse> getCategoryTree();
    List<ProductCategoryResponse> getSoftDeletedCategories();
}
