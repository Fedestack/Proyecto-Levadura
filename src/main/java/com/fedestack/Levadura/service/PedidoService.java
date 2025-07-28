package com.fedestack.Levadura.service;

import com.fedestack.Levadura.model.Cliente;
import com.fedestack.Levadura.model.DetallePedido;
import com.fedestack.Levadura.model.Pedido;
import com.fedestack.Levadura.model.Producto;
import com.fedestack.Levadura.repository.ClienteRepository;
import com.fedestack.Levadura.repository.DetalleRepository;
import com.fedestack.Levadura.repository.PedidoRepository;
import com.fedestack.Levadura.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private DetalleRepository detalleRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProductoRepository productoRepository;

    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public void deletePedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllPedidos() {
        detalleRepository.deleteAll(); // Eliminar detalles primero si hay restricciones de clave externa
        pedidoRepository.deleteAll();
    }

    public Optional<Pedido> getPedidoById(Long id) {
        return pedidoRepository.findById(id);
    }

    public void updatePedidoEstado(Long pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }

    public List<Pedido> getPedidosByClienteId(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Pedido savePedido(Pedido pedido, List<DetallePedido> detalles) {
        log.info("Iniciando savePedido para pedido ID: {} con {} detalles.", pedido.getId(), detalles != null ? detalles.size() : 0);

        // Si el pedido ya tiene un ID, significa que es una edición
        if (pedido.getId() != null) {
            // Recuperar el pedido existente de la base de datos
            Pedido existingPedido = pedidoRepository.findById(pedido.getId())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado para edición."));
            log.info("Editando pedido existente con ID: {}. Detalles actuales: {}.", existingPedido.getId(), existingPedido.getDetalles().size());

            // Actualizar los campos del pedido existente con los nuevos valores
            existingPedido.setCliente(pedido.getCliente());
            existingPedido.setObservaciones(pedido.getObservaciones());
            // La fecha y el estado se mantienen o se actualizan según la lógica de negocio
            pedido = existingPedido; // Usar el pedido existente para la actualización

            // Limpiar los detalles existentes y añadir los nuevos. orphanRemoval=true se encargará de eliminar los antiguos.
            pedido.getDetalles().clear();
            log.info("Detalles del pedido existente limpiados. Ahora {} detalles.", pedido.getDetalles().size());
        } else {
            // Si es un nuevo pedido, establecer fecha y estado inicial
            pedido.setFecha(LocalDateTime.now());
            pedido.setEstado("INGRESADO");
            log.info("Creando nuevo pedido.");
        }

        BigDecimal totalPedido = BigDecimal.ZERO;

        if (detalles != null) {
            for (DetallePedido detalle : detalles) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                detalle.setPrecioCongelado(producto.getPrecioUnitario());
                totalPedido = totalPedido.add(producto.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));
            }
        }

        pedido.setTotal(totalPedido);
        log.info("Total calculado para el pedido: {}.", totalPedido);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.info("Pedido guardado/actualizado con ID: {}.", pedidoGuardado.getId());

        if (detalles != null) {
            for (DetallePedido detalle : detalles) {
                detalle.setPedido(pedidoGuardado);
                // No necesitamos guardar cada detalle individualmente si orphanRemoval=true y la colección se maneja correctamente
                pedidoGuardado.getDetalles().add(detalle);
            }
        }
        log.info("Detalles añadidos a la colección del pedido guardado. Total de detalles en colección: {}.", pedidoGuardado.getDetalles().size());

        return pedidoGuardado;
    }
}