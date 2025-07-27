package com.fedestack.Levadura.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fedestack.Levadura.dto.DetallePedidoDTO;
import com.fedestack.Levadura.model.Cliente;
import com.fedestack.Levadura.model.DetallePedido;
import com.fedestack.Levadura.model.Pedido;
import com.fedestack.Levadura.model.PedidoAutomatico;
import com.fedestack.Levadura.service.ClienteService;
import com.fedestack.Levadura.service.PedidoAutomaticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Controller
@RequestMapping("/cliente/pedidos-automaticos")
public class PedidoAutomaticoClienteController {

    @Autowired
    private PedidoAutomaticoService pedidoAutomaticoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    // ID de cliente fijo para pruebas (debería venir de la sesión en un entorno real)
    private static final Long CLIENTE_ID_FIJO = 1L; 

    @GetMapping
    public String listarPedidosAutomaticos(Model model) {
        List<PedidoAutomatico> pedidosAutomaticos = pedidoAutomaticoService.getPedidosAutomaticosByClienteId(CLIENTE_ID_FIJO);
        model.addAttribute("pedidosAutomaticos", pedidosAutomaticos);
        return "cliente/pedidos_automaticos";
    }

    @PostMapping("/crear-pedido")
    public String crearPedidoDesdeAutomatico(@RequestParam Long pedidoAutomaticoId,
                                             RedirectAttributes redirectAttributes) {
        try {
            // Aquí deberías obtener el clienteId de la sesión del usuario logueado
            // Por ahora, usamos el ID fijo para pruebas
            Long clienteId = CLIENTE_ID_FIJO;

            Pedido nuevoPedido = pedidoAutomaticoService.crearPedidoDesdeAutomatico(pedidoAutomaticoId, clienteId);

            // Redirigir a la pantalla de revisión con los datos del nuevo pedido
            redirectAttributes.addFlashAttribute("pedido", nuevoPedido);
            redirectAttributes.addFlashAttribute("pedidoId", nuevoPedido.getId());
            redirectAttributes.addFlashAttribute("clienteId", nuevoPedido.getCliente().getId());
            redirectAttributes.addFlashAttribute("observaciones", nuevoPedido.getObservaciones());

            // Convertir detalles a JSON para pasarlos al formulario de revisión
            List<DetallePedidoDTO> detallesDTOs = new ArrayList<>();
            for (DetallePedido detalle : nuevoPedido.getDetalles()) {
                DetallePedidoDTO dto = new DetallePedidoDTO();
                dto.setProductoId(detalle.getProducto().getId());
                dto.setCantidad(detalle.getCantidad());
                detallesDTOs.add(dto);
            }
            redirectAttributes.addFlashAttribute("detallesJson", objectMapper.writeValueAsString(detallesDTOs));

            return "redirect:/pedidos/revisar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el pedido desde automático: " + e.getMessage());
            return "redirect:/cliente/pedidos-automaticos";
        }
    }
}
