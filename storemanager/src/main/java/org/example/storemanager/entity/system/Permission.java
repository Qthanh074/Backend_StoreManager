package org.example.storemanager.entity.system;

import jakarta.persistence.*;
import lombok.*;
import org.example.storemanager.entity.BaseEntity;

@Entity
@Table(name = "permissions", indexes = {
        @Index(name = "idx_permissions_code", columnList = "permission_code", unique = true),
        @Index(name = "idx_permissions_module", columnList = "module")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {

    @Column(name = "permission_code", nullable = false, unique = true, length = 100)
    private String permissionCode;

    @Column(nullable = false, length = 50)
    private String module;

    @Column(columnDefinition = "TEXT")
    private String description;
}