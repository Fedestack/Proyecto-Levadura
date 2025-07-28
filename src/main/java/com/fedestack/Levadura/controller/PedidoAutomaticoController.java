package com.fedestack.Levadura.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fedestack.Levadura.dto.DetallePedidoDTO;
import com.fedestack.Levadura.dto.PedidoRequestDTO;
import com.fedestack.Levadura.model.Cliente;
import com.fedestack.Levadura.model.DetallePedidoAutomatico;
import com.fedestack.Levadura.model.PedidoAutomatico;
import com.fedestack.Levadura.model.Producto;
import com.fedestack.Levadura.repository.ClienteRepository;
import com.fedestack.Levadura.repository.ProductoRepository;
import com.fedestack.Levadura.service.PedidoAutomaticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pedidos-automaticos")
public class PedidoAutomaticoController {

    @Autowired
    private PedidoAutomaticoService pedidoAutomaticoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/guardar")
    public ResponseEntity<String> guardarPedidoAutomatico(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        try {
            PedidoAutomatico pedidoAutomatico = new PedidoAutomatico();
            pedidoAutomatico.setNombre(pedidoRequestDTO.getNombre()); // Asumimos que el DTO tiene un campo nombre

            // Asignar el cliente
            Long clienteId = pedidoRequestDTO.getClienteId();
            if (clienteId == null) {
                throw new IllegalArgumentException("El ID del cliente no puede ser nulo.");
            }
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));
            pedidoAutomatico.setCliente(cliente);

            pedidoAutomatico.setObservaciones(pedidoRequestDTO.getObservaciones());

            List<DetallePedidoAutomatico> detallesAutomaticos = new ArrayList<>();
            for (PedidoRequestDTO.DetalleItemDTO itemDTO : pedidoRequestDTO.getDetalles()) {
                if (itemDTO.getProductoId() == null) {
                    throw new IllegalArgumentException("El ID del producto en el detalle no puede ser nulo.");
                }
                Producto producto = productoRepository.findById(itemDTO.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + itemDTO.getProductoId()));

                DetallePedidoAutomatico detalleAutomatico = new DetallePedidoAutomatico();
                detalleAutomatico.setPedidoAutomatico(pedidoAutomatico);
                detalleAutomatico.setProducto(producto);
                detalleAutomatico.setCantidad(itemDTO.getCantidad());
                detallesAutomaticos.add(detalleAutomatico);
            }
            pedidoAutomatico.setDetalles(detallesAutomaticos);

            pedidoAutomaticoService.savePedidoAutomatico(pedidoAutomatico);
            return ResponseEntity.ok("Pedido Automático guardado con éxito.");
        } catch (Exception e) {
            e.printStackTrace(); // Imprimir la traza completa de la excepción
            return ResponseEntity.badRequest().body("Error al guardar el Pedido Automático: " + e.getMessage());
        }
    }
}