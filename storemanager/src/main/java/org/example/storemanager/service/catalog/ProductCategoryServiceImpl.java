package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.ProductCategoryRequest;
import org.example.storemanager.dto.response.catalog.ProductCategoryResponse;
import org.example.storemanager.entity.catalog.ProductCategory;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.catalog.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public List<ProductCategoryResponse> getAllCategories() {
        return categoryRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryResponse getCategoryById(Long id) {
        ProductCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Nhóm sản phẩm", "id", id));
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public ProductCategoryResponse createCategory(ProductCategoryRequest request) {
        ProductCategory parent = null;

        // Ưu tiên tìm theo parentId
        if (request.getParentId() != null) {
            parent = categoryRepository.findByIdAndIsDeletedFalse(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Nhóm cha", "id", request.getParentId()));
        }
        // Nếu không có parentId nhưng có parentName → tự động tạo danh mục cha mới
        else if (request.getParentName() != null && !request.getParentName().trim().isEmpty()) {
            String parentCode = "CAT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            ProductCategory newParent = ProductCategory.builder()
                    .categoryCode(parentCode)
                    .categoryName(request.getParentName().trim())
                    .description("Tự động tạo từ yêu cầu tạo danh mục con")
                    .build();
            newParent.setIsActive(true);
            newParent.setCreatedBy(getCurrentUser());
            parent = categoryRepository.save(newParent);
        }

        // Tự sinh mã danh mục
        String categoryCode = "CAT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ProductCategory category = ProductCategory.builder()
                .categoryCode(categoryCode)
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .parent(parent)
                .department(request.getDepartment())
                .manager(request.getManager())
                .inventoryGlCode(request.getInventoryGlCode())
                .cogsGlCode(request.getCogsGlCode())
                .taxClass(request.getTaxClass())
                .build();
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        category.setCreatedBy(getCurrentUser());

        ProductCategory saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ProductCategoryResponse updateCategory(Long id, ProductCategoryRequest request) {
        ProductCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Nhóm sản phẩm", "id", id));

        ProductCategory parent = null;
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("Nhóm sản phẩm không thể làm nhóm cha của chính nó");
            }
            parent = categoryRepository.findByIdAndIsDeletedFalse(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Nhóm cha", "id", request.getParentId()));
        }

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setParent(parent);
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
        category.setDepartment(request.getDepartment());
        category.setManager(request.getManager());
        category.setInventoryGlCode(request.getInventoryGlCode());
        category.setCogsGlCode(request.getCogsGlCode());
        category.setTaxClass(request.getTaxClass());
        category.setUpdatedBy(getCurrentUser());

        ProductCategory saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ProductCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Nhóm sản phẩm", "id", id));

        if (category.getIsActive() != null && category.getIsActive()) {
            throw new org.example.storemanager.exception.BusinessException(ErrorCode.BUSINESS_ERROR, "Không thể xóa nhóm sản phẩm đang hoạt động. Vui lòng chuyển trạng thái hoạt động (isActive) sang false trước khi xóa.");
        }

        // Thiết lập trạng thái xóa mềm, ghi nhận người xóa và ngày xóa
        category.setIsDeleted(true);
        category.setDeletedAt(LocalDateTime.now());
        category.setDeletedBy(getCurrentUser());

        categoryRepository.save(category);
    }

    @Override
    public List<ProductCategoryResponse> getRootCategories() {
        return categoryRepository.findAllByIsDeletedFalse().stream()
                .filter(c -> c.getParent() == null)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryResponse> getChildrenOf(Long parentId) {
        categoryRepository.findByIdAndIsDeletedFalse(parentId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Nhóm cha", "id", parentId));
        return categoryRepository.findAllByIsDeletedFalse().stream()
                .filter(c -> c.getParent() != null && c.getParent().getId().equals(parentId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryResponse> getCategoryTree() {
        List<ProductCategory> allCategories = categoryRepository.findAllByIsDeletedFalse();
        
        List<ProductCategoryResponse> responses = allCategories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        java.util.Map<Long, ProductCategoryResponse> responseMap = responses.stream()
                .collect(Collectors.toMap(ProductCategoryResponse::getId, r -> r));
        
        List<ProductCategoryResponse> roots = new java.util.ArrayList<>();
        
        for (ProductCategoryResponse response : responses) {
            if (response.getParentId() == null) {
                roots.add(response);
            } else {
                ProductCategoryResponse parentResp = responseMap.get(response.getParentId());
                if (parentResp != null) {
                    if (parentResp.getChildren() == null) {
                        parentResp.setChildren(new java.util.ArrayList<>());
                    }
                    parentResp.getChildren().add(response);
                }
            }
        }
        return roots;
    }

    @Override
    public List<ProductCategoryResponse> getSoftDeletedCategories() {
        return categoryRepository.findAllByIsDeletedTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductCategoryResponse mapToResponse(ProductCategory category) {
        if (category == null) return null;
        return ProductCategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getCategoryName() : null)
                .isActive(category.getIsActive())
                .productCount(0) // Mặc định là 0 hoặc tính toán nếu cần
                .children(new java.util.ArrayList<>())
                .department(category.getDepartment())
                .manager(category.getManager())
                .inventoryGlCode(category.getInventoryGlCode())
                .cogsGlCode(category.getCogsGlCode())
                .taxClass(category.getTaxClass())
                .deletedAt(category.getDeletedAt())
                .deletedBy(category.getDeletedBy())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .updatedAt(category.getUpdatedAt())
                .updatedBy(category.getUpdatedBy())
                .build();
    }
}
