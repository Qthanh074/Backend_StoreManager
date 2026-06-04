package org.example.storemanager.entity.catalog;

import jakarta.persistence.*;
import lombok.*;
import org.example.storemanager.entity.BaseEntity;

@Entity
@Table(name = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Unit extends BaseEntity {

    @Column(name = "unit_code", nullable = false, unique = true, length = 50)
    private String unitCode;

    @Column(name = "unit_name", nullable = false, length = 50)
    private String unitName;
}