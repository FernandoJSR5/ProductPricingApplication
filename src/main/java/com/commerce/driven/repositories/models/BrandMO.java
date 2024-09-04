package com.commerce.driven.repositories.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Brand mo.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "brands")
public class BrandMO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String brandName;

}
