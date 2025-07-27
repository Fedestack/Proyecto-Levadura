package com.fedestack.Levadura.service;

import com.fedestack.Levadura.model.DetallePedido;
import com.fedestack.Levadura.model.DetallePedidoAutomatico;
import com.fedestack.Levadura.model.Pedido;
import com.fedestack.Levadura.model.PedidoAutomatico;
import com.fedestack.Levadura.model.Producto;
import com.fedestack.Levadura.repository.PedidoAutomaticoRepository;
import com.fedestack.Levadura.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoAutomaticoService {

    @Autowired
    private PedidoAutomaticoRepository pedidoAutomaticoRepository;

    @Autowired
    private PedidoService pedidoService; // Para crear pedidos reales

    @Autowired
    private ProductoRepository productoRepository;

    public PedidoAutomatico savePedidoAutomatico(PedidoAutomatico pedidoAutomatico) {
        return pedidoAutomaticoRepository.save(pedidoAutomatico);
    }

    public Optional<PedidoAutomatico> getPedidoAutomaticoById(Long id) {
        return pedidoAutomaticoRepository.findById(id);
    }

    public List<PedidoAutomatico> getPedidosAutomaticosByClienteId(Long clienteId) {
        return pedidoAutomaticoRepository.findByClienteId(clienteId);
    }

    public void deletePedidoAutomatico(Long id) {
        pedidoAutomaticoRepository.deleteById(id);
    }

    @Transactional
    public Pedido crearPedidoDesdeAutomatico(Long pedidoAutomaticoId, Long clienteId) {
        PedidoAutomatico pa = pedidoAutomaticoRepository.findById(pedidoAutomaticoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido Automático no encontrado."));

        if (!pa.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("El pedido automático no pertenece a este cliente.");
        }

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(pa.getCliente());
        nuevoPedido.setObservaciones(pa.getObservaciones());
        // El estado y la fecha se establecerán en PedidoService.savePedido

        List<DetallePedido> detallesReales = new ArrayList<>();
        for (DetallePedidoAutomatico dpa : pa.getDetalles()) {
            Producto producto = productoRepository.findById(dpa.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado en el catálogo actual."));

            DetallePedido dp = new DetallePedido();
            dp.setProducto(producto);
            dp.setCantidad(dpa.getCantidad());
            dp.setPrecioCongelado(producto.getPrecioUnitario()); // Tomar el precio actual del producto
            detallesReales.add(dp);
        }

        return pedidoService.savePedido(nuevoPedido, detallesReales);
    }
}
