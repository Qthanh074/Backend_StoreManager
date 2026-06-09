package org.example.storemanager.entity.catalog;

import jakarta.persistence.*;
import lombok.*;
import org.example.storemanager.entity.BaseEntity;

@Entity
@Table(name = "serial_numbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class SerialNumber extends BaseEntity {

    @Column(name = "serial_number", nullable = false, length = 100)
    private String serialNumber; // Mã IMEI hoặc số mã định danh sản phẩm độc bản

    @Column(length = 30)
    private String status; // Trạng thái: AVAILABLE, SOLD, WARRANTY, RETURNED...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "import_receipt_id")
    private Long importReceiptId; // Khóa ngoại kết nối sang phân hệ Kho (ImportReceipt) sau này
}