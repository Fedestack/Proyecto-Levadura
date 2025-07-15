package com.fedestack.Levadura.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity

public class Cliente {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String nombre;

   private String direccion;
   private String telefono;
   private String cuit;

   // --- Relaciones ---
   @OneToOne(fetch = FetchType.LAZY) // Un Cliente tiene una cuenta de Usuario. Fetch.LAZY para eficiencia.
   @JoinColumn(name = "usuario_id", referencedColumnName = "id") // Así se llamará la clave foránea en la tabla Cliente
   @JsonIgnore // Evita problemas de bucles infinitos al convertir a JSON
   private Usuario usuario;

}
