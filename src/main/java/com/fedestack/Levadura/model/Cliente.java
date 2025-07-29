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

    @Column(unique = true)
    private String codigo; // Código del cliente del CSV

    private String razonSocial; // Razón Social / Nombre del comercio
    private String nombreCompleto; // Nombre y Apellido del cliente/contacto

    private String domicilio;
    private String localidad;
    private String cuit;
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_de_precios_excel_id")
    private ListaDePrecios listaPrecioExcel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_de_precios_gas_id")
    private ListaDePrecios listaPrecioGas;
    private String formaDePago;
    private String frecuenciaDePago;
    private String tipoFactura; // A, B, C
    private String horarioEntrega;
    private String reparto;

    @Column(length = 1024) // Aumentar longitud para comentarios largos
    private String comentarios;


    // --- Relaciones ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @JsonIgnore
    private Usuario usuario;

}