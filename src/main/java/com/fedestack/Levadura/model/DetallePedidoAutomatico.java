package com.fedestack.Levadura.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_pedidos_automaticos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoAutomatico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_automatico_id", nullable = false)
    private PedidoAutomatico pedidoAutomatico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private int cantidad;

    // No almacenamos precioCongelado aquí, ya que el precio se toma del Producto actual al crear el pedido real.
}
