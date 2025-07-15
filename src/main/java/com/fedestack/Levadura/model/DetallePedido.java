package com.fedestack.Levadura.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity

public class DetallePedido {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private int cantidad;
   private BigDecimal precioCongelado; // El precio del producto al momento de la compra

   // --- Relaciones ---
   @ManyToOne
   @JoinColumn(name = "pedido_id", nullable = false)
   @JsonBackReference // La otra parte de la relación con Pedido para evitar bucles
   private Pedido pedido;

   @ManyToOne
   @JoinColumn(name = "producto_id", nullable = false)
   private Producto producto;
}
