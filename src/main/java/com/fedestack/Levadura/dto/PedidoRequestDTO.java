package com.fedestack.Levadura.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private Long id;
    private String nombre; // Nuevo campo para el nombre del pedido automático
    private Long clienteId;
    private String observaciones;
    private List<DetalleItemDTO> detalles;

    @Data
    public static class DetalleItemDTO {
        private Long productoId;
        private int cantidad;
    }
}
