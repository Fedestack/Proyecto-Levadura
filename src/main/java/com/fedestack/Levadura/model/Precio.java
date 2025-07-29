package com.fedestack.Levadura.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"producto_id", "lista_de_precios_id"})
})
public class Precio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_de_precios_id")
    private ListaDePrecios listaDePrecios;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
}
