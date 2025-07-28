package com.fedestack.Levadura.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity

public class Pedido {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private LocalDateTime fecha;
   private String estado; // Ej: "INGRESADO", "EN_PREPARACION", "ENTREGADO"
   private BigDecimal total;
   private String observaciones;

   // --- Relaciones ---
   @ManyToOne // Muchos Pedidos pueden ser de un Cliente
   @JoinColumn(name = "cliente_id", nullable = false) // La clave foránea
   private Cliente cliente;

   @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
   // Un Pedido tiene muchos detalles.
   // Cascade=ALL: si borro un pedido, se borran sus detalles.
   // mappedBy="pedido": le dice a JPA que la relación ya está definida en el campo "pedido" de la clase DetallePedido.
   @JsonManagedReference // Gestiona la serialización para evitar bucles
   private List<DetallePedido> detalles = new ArrayList<>();

}
