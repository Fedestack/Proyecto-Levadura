package com.fedestack.Levadura.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre; // Corresponde a "Detalle Articulo"

    private String rubro; // Corresponde a "Rubro"

    private String unidad; // Corresponde a "Unidad" en el CSV

    @Column(precision = 10, scale = 2)
    private BigDecimal costoTotal; // El costo interno del producto

    // Un producto tiene muchos precios. Si se borra el producto, se borran sus precios.
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Precio> precios = new ArrayList<>();

}