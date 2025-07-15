package com.fedestack.Levadura.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity

public class Usuario {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(unique = true, nullable = false)
   private String email;

   @Column(nullable = false)
   private String password; // Guardaremos la contraseña hasheada, no en texto plano

   private String rol; // Ej: "ROLE_CLIENTE", "ROLE_ADMIN"
}
