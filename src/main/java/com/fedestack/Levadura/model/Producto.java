package com.fedestack.Levadura.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data // Lombok: Genera getters, setters, toString(), etc.
@Entity // JPA: Marca esta clase como una tabla de la base de datos

public class Producto {

   @Id // Marca este campo como la clave primaria
   @GeneratedValue(strategy = GenerationType.IDENTITY) // Le dice a MySQL que genere el ID automáticamente
   private Long id;
   @Column(unique = true, nullable = false) // El código debe ser único y no puede ser nulo
   private String codigo;

   @Column(nullable = false)
   private String nombre;

   private String descripcion;

   @Column(nullable = false)
   private BigDecimal precioUnitario; // Usamos BigDecimal para dinero, ¡nunca double!

   private String unidad; // Ej: "KG", "DOCENA", "UNIDAD"

}
