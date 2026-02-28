package com.clinic.Model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_management", indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_name", columnList = "name")
})
@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    @Column(name = "inventory_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "minStock")
    private int minStock;

    @Column(name = "unit")
    private String unit;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "expiryDate")
    private LocalDate expiryDate;

    @Column(name = "supplier")
    private String supplier;

    @Column(name = "batchNumber")
    private String batchNumber;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Column(name = "status")
    private String status;

    @Column(name = "location")
    private String location;

    @Column(name = "reorder_point")
    private Integer reorderPoint;
}
