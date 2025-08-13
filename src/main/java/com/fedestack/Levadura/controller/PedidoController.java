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
import com.fedestack.Levadura.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fedestack.Levadura.repository.UsuarioRepository;
import com.fedestack.Levadura.repository.ListaDePreciosRepository;
import com.fedestack.Levadura.model.Usuario;
import com.fedestack.Levadura.model.ListaDePrecios;
import com.fedestack.Levadura.dto.ProductoDisplayDTO;
import java.security.Principal;

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
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper; // Jackson's ObjectMapper

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ListaDePreciosRepository listaDePreciosRepository;

    @GetMapping("")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        model.addAttribute("pedidos", pedidos);
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioDeNuevoPedido(Principal principal,
                                                 @RequestParam(name = "observaciones", required = false) String observaciones,
                                                 @RequestParam(name = "detallesJson", required = false) String detallesJson,
                                                 Model model) throws IOException {

        Cliente cliente;
        if (principal != null) {
            String username = principal.getName();
            Usuario usuario = usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
            cliente = usuario.getCliente();
            if (cliente == null) {
                throw new RuntimeException("El usuario logueado no tiene un cliente asociado.");
            }
        } else {
            // Si no hay usuario logueado, usar un cliente por defecto (ej. el primero o un cliente 'público')
            // O lanzar una excepción si el acceso sin cliente no está permitido.
            cliente = clienteRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No hay clientes en la base de datos y no hay usuario logueado."));
        }

        // Obtener todos los productos y calcular el precio específico para el cliente
        List<Producto> allProducts = pedidoService.getAllProductos();
        List<ProductoDisplayDTO> productosParaDisplay = new ArrayList<>();
        for (Producto p : allProducts) {
            ProductoDisplayDTO dto = new ProductoDisplayDTO();
            dto.setId(p.getId());
            dto.setCodigo(p.getCodigo());
            dto.setNombre(p.getNombre());
            dto.setUnidad(p.getUnidad());
            dto.setPrecioUnitario(p.getMayorista()); // Usar precio Mayorista por defecto
            productosParaDisplay.add(dto);
        }
        model.addAttribute("productos", productosParaDisplay);

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
                // Usar el precio ya calculado para el cliente logueado
                BigDecimal precioCongelado = productoService.getPrecioForProductoAndLista(producto, cliente.getListaPrecioExcel());
                detalle.setPrecioCongelado(precioCongelado);
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
    public String mostrarFormularioDeEditarPedido(Principal principal, HttpServletRequest request, Model model) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long id = Long.valueOf(pathVariables.get("id"));
        Pedido pedido = pedidoService.getPedidoById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Pedido no válido:" + id));

        // Obtener el cliente asociado al usuario logueado
        Cliente cliente;
        if (principal != null) {
            String username = principal.getName();
            Usuario usuario = usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
            cliente = usuario.getCliente();
            if (cliente == null) {
                throw new RuntimeException("El usuario logueado no tiene un cliente asociado.");
            }
        } else {
            // Si no hay usuario logueado, usar un cliente por defecto (ej. el primero o un cliente 'público')
            // O lanzar una excepción si el acceso sin cliente no está permitido.
            cliente = clienteRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No hay clientes en la base de datos y no hay usuario logueado."));
        }

        // Obtener todos los productos y calcular el precio específico para el cliente
        List<Producto> allProducts = pedidoService.getAllProductos();
        List<ProductoDisplayDTO> productosParaDisplay = new ArrayList<>();
        for (Producto p : allProducts) {
            ProductoDisplayDTO dto = new ProductoDisplayDTO();
            dto.setId(p.getId());
            dto.setCodigo(p.getCodigo());
            dto.setNombre(p.getNombre());
            dto.setUnidad(p.getUnidad());
            dto.setPrecioUnitario(p.getMayorista()); // Usar precio Mayorista por defecto
            productosParaDisplay.add(dto);
        }
        model.addAttribute("productos", productosParaDisplay);

        model.addAttribute("pedido", pedido);
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
                                @RequestParam(value = "clienteId", required = false) Long clienteId,
                                @RequestParam(value = "observaciones", required = false) String observaciones,
                                @RequestParam(value = "detallesJson", required = false) String detallesJson,
                                RedirectAttributes redirectAttributes) throws IOException {

        // Asignar cliente (usando el primero si no se provee, como en el método de guardar)
        final Long finalClienteId;
        if (clienteId == null) {
            final Long CLIENTE_ID_FIJO = 1L; // Definir aquí o importar si es una constante global
            finalClienteId = clienteRepository.findAll().stream().findFirst()
                    .map(Cliente::getId)
                    .orElse(CLIENTE_ID_FIJO); // Usar el ID fijo si no hay clientes en la base de datos
        } else {
            finalClienteId = clienteId;
        }

        // Manejar observaciones nulas o vacías
        if (observaciones == null || observaciones.trim().isEmpty()) {
            observaciones = "Sin observaciones";
        }

        // Manejar detallesJson nulos o vacíos
        if (detallesJson == null || detallesJson.trim().isEmpty()) {
            detallesJson = "[]";
        }

        // Construir el objeto Pedido (sin persistir) para la vista de confirmación
        Pedido pedidoDeConfirmacion = new Pedido();
        pedidoDeConfirmacion.setId(pedidoId);
        pedidoDeConfirmacion.setObservaciones(observaciones);

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
            // Obtener la lista de precios del cliente
            Cliente clienteParaPrecio = clienteRepository.findById(finalClienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + finalClienteId));
            if (clienteParaPrecio.getListaPrecioExcel() == null) {
                throw new IllegalStateException("El cliente no tiene una lista de precios Excel asignada.");
            }
            BigDecimal precioUnitario = productoService.getPrecioForProductoAndLista(producto, clienteParaPrecio.getListaPrecioExcel());

            detalle.setPrecioCongelado(precioUnitario); // Usamos el precio actual para la previsualización
            detalles.add(detalle);
            BigDecimal cantidad = new BigDecimal(dto.getCantidad());
            total = total.add(precioUnitario.multiply(cantidad));
        }

        pedidoDeConfirmacion.setDetalles(detalles);
        pedidoDeConfirmacion.setTotal(total);

        // Pasamos los datos del pedido a través de atributos de redirección (como parámetros de URL)
        redirectAttributes.addAttribute("pedidoId", pedidoId);
        redirectAttributes.addAttribute("clienteId", finalClienteId);
        redirectAttributes.addAttribute("observaciones", observaciones);
        redirectAttributes.addAttribute("detallesJson", detallesJson);

        return "redirect:/pedidos/revisar";
    }

    @GetMapping("/revisar")
    public String revisarPedidoGet(@RequestParam(value = "pedidoId", required = false) Long pedidoId,
                                   @RequestParam(value = "clienteId", required = false) Long clienteId,
                                   @RequestParam(value = "observaciones", required = false) String observaciones,
                                   @RequestParam(value = "detallesJson", required = false) String detallesJson,
                                   Model model) throws IOException {

        // Manejar observaciones nulas o vacías
        if (observaciones == null || observaciones.trim().isEmpty()) {
            observaciones = "Sin observaciones";
        }

        // Manejar detallesJson nulos o vacíos
        if (detallesJson == null || detallesJson.trim().isEmpty()) {
            detallesJson = "[]";
        }

        // Asignar cliente (usando el primero si no se provee, como en el método de guardar)
        final Long finalClienteId;
        if (clienteId == null) {
            final Long CLIENTE_ID_FIJO = 1L; // Definir aquí o importar si es una constante global
            finalClienteId = clienteRepository.findAll().stream().findFirst()
                    .map(Cliente::getId)
                    .orElse(CLIENTE_ID_FIJO); // Usar el ID fijo si no hay clientes en la base de datos
        } else {
            finalClienteId = clienteId;
        }

        Pedido pedidoDeConfirmacion = new Pedido();
        pedidoDeConfirmacion.setId(pedidoId);
        pedidoDeConfirmacion.setObservaciones(observaciones);

        // Asignar cliente
        Cliente cliente = clienteRepository.findById(finalClienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + finalClienteId));
        pedidoDeConfirmacion.setCliente(cliente);

        // Deserializar los detalles del JSON
        System.out.println("Detalles JSON recibido en revisarPedidoGet: " + detallesJson);
        List<DetallePedidoDTO> detalleDTOs = objectMapper.readValue(detallesJson, new TypeReference<List<DetallePedidoDTO>>(){});
        System.out.println("Detalles DTOs después de deserialización: " + detalleDTOs);
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (DetallePedidoDTO dto : detalleDTOs) {
            Producto producto = productoRepository.findById(Long.valueOf(dto.getProductoId()))
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductoId()));
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(dto.getCantidad());
            // Obtener la lista de precios del cliente
            Cliente clienteParaPrecio = clienteRepository.findById(finalClienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + finalClienteId));
            if (clienteParaPrecio.getListaPrecioExcel() == null) {
                throw new IllegalStateException("El cliente no tiene una lista de precios Excel asignada.");
            }
            BigDecimal precioUnitario = productoService.getPrecioForProductoAndLista(producto, clienteParaPrecio.getListaPrecioExcel());

            detalle.setPrecioCongelado(precioUnitario); // Usamos el precio actual para la previsualización
            detalles.add(detalle);
            BigDecimal cantidad = new BigDecimal(dto.getCantidad());
            total = total.add(precioUnitario.multiply(cantidad));
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

    @PostMapping("/confirmar/{id}")
    public String confirmarPedido(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.updatePedidoEstado(id, "EN_PREPARACION");
            redirectAttributes.addFlashAttribute("mensaje", "Pedido #" + id + " confirmado y en preparación.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al confirmar pedido: " + e.getMessage());
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/entregar/{id}")
    public String entregarPedido(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.updatePedidoEstado(id, "ENTREGADO");
            redirectAttributes.addFlashAttribute("mensaje", "Pedido #" + id + " marcado como entregado.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al entregar pedido: " + e.getMessage());
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/eliminar-todos")
    public String eliminarTodosLosPedidos() {
        pedidoService.deleteAllPedidos();
        return "redirect:/pedidos";
    }
}