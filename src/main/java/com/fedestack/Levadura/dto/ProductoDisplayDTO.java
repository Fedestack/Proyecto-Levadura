package com.fedestack.Levadura.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDisplayDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String unidad;
    private BigDecimal precioUnitario;
}
