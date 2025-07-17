package com.fedestack.Levadura.controller;

import com.fedestack.Levadura.dto.PedidoRequestDTO;
import com.fedestack.Levadura.model.Cliente;
import com.fedestack.Levadura.model.DetallePedido;
import com.fedestack.Levadura.model.Pedido;
import com.fedestack.Levadura.model.Producto;
import com.fedestack.Levadura.repository.ClienteRepository;
import com.fedestack.Levadura.repository.ProductoRepository;
import com.fedestack.Levadura.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        model.addAttribute("pedidos", pedidos);
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioDeNuevoPedido(Model model) {
        // Ya no pasamos la lista de clientes al frontend, se asume que el cliente está logueado o se asigna en backend
        List<Producto> productos = pedidoService.getAllProductos();
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("productos", productos);
        return "pedidos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEditarPedido(HttpServletRequest request, Model model) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long id = Long.valueOf(pathVariables.get("id"));
        Pedido pedido = pedidoService.getPedidoById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Pedido no válido:" + id));
        List<Producto> productos = pedidoService.getAllProductos();
        model.addAttribute("pedido", pedido);
        model.addAttribute("productos", productos);
        return "pedidos/formulario";
    }

    @GetMapping("/ver/{id}")
    public String verDetallePedido(HttpServletRequest request, Model model) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long id = Long.valueOf(pathVariables.get("id"));
        Pedido pedido = pedidoService.getPedidoById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Pedido no válido:" + id));
        model.addAttribute("pedido", pedido);
        return "pedidos/ver_detalle";
    }

    @PostMapping("/guardar")
    @ResponseBody // Indica que el método devuelve directamente el cuerpo de la respuesta (no una vista)
    public String guardarPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        Pedido pedido = new Pedido();
        pedido.setId(pedidoRequestDTO.getId()); // <--- AÑADIR ESTA LÍNEA
        // Asignar el cliente al pedido
        Long tempClienteId = pedidoRequestDTO.getClienteId();
        // TEMPORAL: Si no viene clienteId del frontend (porque no hay selector), asignamos el primer cliente disponible para pruebas.
        if (tempClienteId == null) {
            tempClienteId = clienteRepository.findAll().stream().findFirst()
                    .map(Cliente::getId)
                    .orElseThrow(() -> new RuntimeException("No hay clientes en la base de datos para asignar al pedido."));
        }
        final Long finalClienteId = tempClienteId;
        Cliente cliente = clienteRepository.findById(finalClienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + finalClienteId));
        pedido.setCliente(cliente);
        pedido.setObservaciones(pedidoRequestDTO.getObservaciones());

        List<DetallePedido> detalles = new ArrayList<>();
        for (PedidoRequestDTO.DetalleItemDTO itemDTO : pedidoRequestDTO.getDetalles()) {
            DetallePedido detalle = new DetallePedido();
            Producto producto = productoRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            detalle.setProducto(producto);
            detalle.setCantidad(itemDTO.getCantidad());
            detalles.add(detalle);
        }

        pedidoService.savePedido(pedido, detalles);
        return "redirect:/pedidos"; // Redirigir después de guardar
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(HttpServletRequest request) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long id = Long.valueOf(pathVariables.get("id"));
        pedidoService.deletePedido(id);
        return "redirect:/pedidos";
    }
}