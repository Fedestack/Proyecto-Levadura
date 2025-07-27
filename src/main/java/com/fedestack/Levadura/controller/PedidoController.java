package com.fedestack.Levadura.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fedestack.Levadura.dto.DetallePedidoDTO;
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
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper; // Jackson's ObjectMapper

    @GetMapping("")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        model.addAttribute("pedidos", pedidos);
        return "pedidos/lista";
    }

    @RequestMapping("/nuevo")
    public String mostrarFormularioDeNuevoPedido(@RequestParam(name = "observaciones", required = false) String observaciones,
                                                 @RequestParam(name = "detallesJson", required = false) String detallesJson,
                                                 Model model) throws IOException {
        List<Producto> productos = pedidoService.getAllProductos();
        model.addAttribute("productos", productos);

        Pedido pedido = new Pedido();
        if (observaciones != null && detallesJson != null) {
            // Si vienen datos, es una edición de un pedido nuevo desde la pantalla de confirmación
            pedido.setObservaciones(observaciones);
            List<DetallePedidoDTO> detalleDTOs = objectMapper.readValue(detallesJson, new TypeReference<List<DetallePedidoDTO>>(){});
            List<DetallePedido> detalles = new ArrayList<>();
            for (DetallePedidoDTO dto : detalleDTOs) {
                Producto producto = productoRepository.findById(Long.valueOf(dto.getProductoId()))
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductoId()));
                DetallePedido detalle = new DetallePedido();
                detalle.setProducto(producto);
                detalle.setCantidad(dto.getCantidad());
                detalle.setPrecioCongelado(producto.getPrecioUnitario()); // Usamos el precio actual
                detalles.add(detalle);
            }
            pedido.setDetalles(detalles);
            // Forzar la inicialización de los IDs de producto para Thymeleaf/JavaScript
            for (DetallePedido detalle : detalles) {
                detalle.getProducto().getId(); // Acceder al ID fuerza la inicialización del proxy
            }
        }
        model.addAttribute("pedido", pedido);
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

    @PostMapping("/revisar")
    public String revisarPedido(@RequestParam(value = "pedidoId", required = false) Long pedidoId,
                                @RequestParam("clienteId") Long clienteId,
                                @RequestParam("observaciones") String observaciones,
                                @RequestParam("detallesJson") String detallesJson,
                                RedirectAttributes redirectAttributes) throws IOException {

        // Construir el objeto Pedido (sin persistir) para la vista de confirmación
        Pedido pedidoDeConfirmacion = new Pedido();
        pedidoDeConfirmacion.setId(pedidoId);
        pedidoDeConfirmacion.setObservaciones(observaciones);

        // Asignar cliente (usando el primero si no se provee, como en el método de guardar)
        final Long finalClienteId;
        if (clienteId == null) {
            finalClienteId = clienteRepository.findAll().stream().findFirst()
                    .map(Cliente::getId)
                    .orElseThrow(() -> new RuntimeException("No hay clientes en la base de datos."));
        } else {
            finalClienteId = clienteId;
        }
        Cliente cliente = clienteRepository.findById(finalClienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + finalClienteId));
        pedidoDeConfirmacion.setCliente(cliente);

        // Deserializar los detalles del JSON
        List<DetallePedidoDTO> detalleDTOs = objectMapper.readValue(detallesJson, new TypeReference<List<DetallePedidoDTO>>(){});
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (DetallePedidoDTO dto : detalleDTOs) {
            Producto producto = productoRepository.findById(Long.valueOf(dto.getProductoId()))
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductoId()));
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(dto.getCantidad());
            detalle.setPrecioCongelado(producto.getPrecioUnitario()); // Usamos el precio actual para la previsualización
            detalles.add(detalle);
            BigDecimal cantidad = new BigDecimal(dto.getCantidad());
            total = total.add(producto.getPrecioUnitario().multiply(cantidad));
        }

        pedidoDeConfirmacion.setDetalles(detalles);
        pedidoDeConfirmacion.setTotal(total);

        // Pasamos los datos del pedido a través de flash attributes para la redirección GET
        redirectAttributes.addFlashAttribute("pedido", pedidoDeConfirmacion);
        redirectAttributes.addFlashAttribute("pedidoId", pedidoId);
        redirectAttributes.addFlashAttribute("clienteId", finalClienteId);
        redirectAttributes.addFlashAttribute("observaciones", observaciones);
        redirectAttributes.addFlashAttribute("detallesJson", detallesJson);

        return "redirect:/pedidos/revisar";
    }

    @GetMapping("/revisar")
    public String revisarPedidoGet(@RequestParam(value = "pedidoId", required = false) Long pedidoId,
                                   @RequestParam("clienteId") Long clienteId,
                                   @RequestParam("observaciones") String observaciones,
                                   @RequestParam("detallesJson") String detallesJson,
                                   Model model) throws IOException {

        Pedido pedidoDeConfirmacion = new Pedido();
        pedidoDeConfirmacion.setId(pedidoId);
        pedidoDeConfirmacion.setObservaciones(observaciones);

        // Asignar cliente
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));
        pedidoDeConfirmacion.setCliente(cliente);

        // Deserializar los detalles del JSON
        List<DetallePedidoDTO> detalleDTOs = objectMapper.readValue(detallesJson, new TypeReference<List<DetallePedidoDTO>>(){});
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (DetallePedidoDTO dto : detalleDTOs) {
            Producto producto = productoRepository.findById(Long.valueOf(dto.getProductoId()))
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductoId()));
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(dto.getCantidad());
            detalle.setPrecioCongelado(producto.getPrecioUnitario()); // Usamos el precio actual para la previsualización
            detalles.add(detalle);
            BigDecimal cantidad = new BigDecimal(dto.getCantidad());
            total = total.add(producto.getPrecioUnitario().multiply(cantidad));
        }

        pedidoDeConfirmacion.setDetalles(detalles);
        pedidoDeConfirmacion.setTotal(total);

        model.addAttribute("pedido", pedidoDeConfirmacion);
        model.addAttribute("pedidoId", pedidoId);
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("observaciones", observaciones);
        model.addAttribute("detallesJson", detallesJson);
        return "pedidos/confirmacion_pedido";
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
