package com.fedestack.Levadura.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ListaDePrecios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Podríamos añadir una descripción si fuera necesario en el futuro
    // private String descripcion;
}
