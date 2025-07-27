package com.fedestack.Levadura.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreLocal;
    private String direccionCompleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
