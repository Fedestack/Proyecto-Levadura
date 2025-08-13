package com.fedestack.Levadura.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String rubro;

    private String unidad;

    @Column(name = "Minorista", precision = 10, scale = 2)
    private BigDecimal minorista;

    @Column(name = "Mayorista", precision = 10, scale = 2)
    private BigDecimal mayorista;

    @Column(name = "AMODIL", precision = 10, scale = 2)
    private BigDecimal amodil;

    @Column(name = "EL_TANO", precision = 10, scale = 2)
    private BigDecimal elTano;

    @Column(name = "VIVANCO", precision = 10, scale = 2)
    private BigDecimal vivanco;

    @Column(name = "GLORIAS", precision = 10, scale = 2)
    private BigDecimal glorias;

    @Column(name = "POMPEYA", precision = 10, scale = 2)
    private BigDecimal pompeya;

    @Column(name = "BRAZZI", precision = 10, scale = 2)
    private BigDecimal brazzi;

    @Column(name = "JUANCHOS", precision = 10, scale = 2)
    private BigDecimal juanchos;

    @Column(name = "GUERE_GUERE_ABUELO", precision = 10, scale = 2)
    private BigDecimal guereGuereAbuelo;

    @Column(name = "TUTTO_SAN_FER", precision = 10, scale = 2)
    private BigDecimal tuttoSanFer;

    @Column(name = "RINCON_LINIERS", precision = 10, scale = 2)
    private BigDecimal rinconLiniers;

    @Column(name = "JOAQUIN_CHINO", precision = 10, scale = 2)
    private BigDecimal joaquinChino;

    @Column(name = "VILLA_TERRA", precision = 10, scale = 2)
    private BigDecimal villaTerra;

}
