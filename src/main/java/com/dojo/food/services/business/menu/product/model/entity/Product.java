package com.dojo.food.services.business.menu.product.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "unique_identifier")
    private String uniqueIdentifier;
    @NotBlank
    @Size(min = 4, max = 50)
    private String name;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Digits(integer = 4, fraction = 2)
    private BigDecimal price;
    @Digits(integer = 1, fraction = 2)
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "0.3", inclusive = true)
    private BigDecimal dscto;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;
    @NotBlank
    private String description;
    private Integer stock;
    private Boolean available;
    @Lob()
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

}
