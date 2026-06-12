package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.request.inventory.CheckLineRequest;
import org.example.storemanager.dto.request.inventory.InventoryCheckRequest;
import org.example.storemanager.dto.response.inventory.InventoryCheckResponse;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.entity.inventory.Inventory;
import org.example.storemanager.entity.inventory.InventoryCheck;
import org.example.storemanager.entity.inventory.InventoryCheckDetail;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.enums.inventory.CheckStatus;
import org.example.storemanager.exception.BusinessException;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.repository.catalog.UnitRepository;
import org.example.storemanager.repository.inventory.InventoryCheckDetailRepository;
import org.example.storemanager.repository.inventory.InventoryCheckRepository;
import org.example.storemanager.repository.inventory.InventoryRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.service.inventory.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryCheckServiceImplTest {

    @Mock
    private InventoryCheckRepository checkRepository;

    @Mock
    private InventoryCheckDetailRepository detailRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryCheckServiceImpl checkService;

    private Branch branch;
    private Product product;
    private InventoryCheck check;
    private InventoryCheckDetail checkDetail;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .branchCode("BR-TEST")
                .branchName("Test Branch")
                .build();
        branch.setId(1L);

        product = Product.builder()
                .productCode("PROD-TEST")
                .name("Test Product")
                .build();
        product.setId(2L);

        check = InventoryCheck.builder()
                .checkCode("CHECK-TEST")
                .checkDate(LocalDateTime.now())
                .status(CheckStatus.DRAFT.name())
                .branch(branch)
                .build();
        check.setId(3L);

        checkDetail = InventoryCheckDetail.builder()
                .check(check)
                .product(product)
                .systemQty(BigDecimal.TEN)
                .actualQty(BigDecimal.valueOf(12)) // thừa 2 cái
                .diffQty(BigDecimal.valueOf(2))
                .build();
        checkDetail.setId(4L);

        inventory = Inventory.builder()
                .branch(branch)
                .product(product)
                .quantity(BigDecimal.TEN)
                .build();
    }

    @Test
    void createCheck_Success() {
        // Arrange
        CheckLineRequest lineRequest = new CheckLineRequest();
        lineRequest.setProductId(2L);
        lineRequest.setActualQuantity(12);

        InventoryCheckRequest request = new InventoryCheckRequest();
        request.setCheckCode("CHECK-TEST");
        request.setBranchId(1L);
        request.setCheckLines(Collections.singletonList(lineRequest));

        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(checkRepository.existsByCheckCodeAndIsDeletedFalse("CHECK-TEST")).thenReturn(false);
        when(checkRepository.save(any(InventoryCheck.class))).thenReturn(check);
        when(productRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByBranchIdAndProductIdAndIsDeletedFalse(1L, 2L)).thenReturn(Optional.of(inventory));
        when(detailRepository.save(any(InventoryCheckDetail.class))).thenReturn(checkDetail);

        // Act
        InventoryCheckResponse response = checkService.createCheck(request);

        // Assert
        assertNotNull(response);
        assertEquals("CHECK-TEST", response.getCheckCode());
        assertEquals(CheckStatus.DRAFT, response.getStatus());
        verify(checkRepository, times(1)).save(any(InventoryCheck.class));
    }

    @Test
    void completeCheck_Success_AdjustStockPlus() {
        // Arrange
        check.setStatus(CheckStatus.IN_PROGRESS.name());
        when(checkRepository.findById(3L)).thenReturn(Optional.of(check));
        when(checkRepository.save(any(InventoryCheck.class))).thenReturn(check);
        when(detailRepository.findByCheckIdAndIsDeletedFalse(3L)).thenReturn(Collections.singletonList(checkDetail));

        // Act
        InventoryCheckResponse response = checkService.completeCheck(3L);

        // Assert
        assertNotNull(response);
        assertEquals(CheckStatus.COMPLETED, response.getStatus());
        // Verify updateStock is called with difference of 2 (STOCK_ADJUSTMENT_PLUS)
        verify(inventoryService, times(1)).updateStock(
                eq(1L),
                eq(2L),
                eq(BigDecimal.valueOf(2)),
                eq("STOCK_ADJUSTMENT_PLUS"),
                eq(3L),
                isNull()
        );
    }

    @Test
    void completeCheck_ThrowsException_WhenStatusNotInProgress() {
        // Arrange
        check.setStatus(CheckStatus.DRAFT.name());
        when(checkRepository.findById(3L)).thenReturn(Optional.of(check));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> checkService.completeCheck(3L));
        assertTrue(ex.getMessage().contains("Chỉ có thể hoàn tất phiếu kiểm kê đang ở trạng thái IN_PROGRESS"));
        verify(inventoryService, never()).updateStock(any(), any(), any(), any(), any(), any());
    }
}
