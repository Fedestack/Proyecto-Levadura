package com.fedestack.Levadura.controller;

import com.fedestack.Levadura.model.Cliente;
import com.fedestack.Levadura.model.Pedido;
import com.fedestack.Levadura.service.ClienteService;
import com.fedestack.Levadura.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Comparator;

@Controller
@RequestMapping("/cliente")
public class ClientePanelController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    // ID de cliente fijo para pruebas
    private static final Long CLIENTE_ID_FIJO = 1L; // Puedes cambiar este ID según tus datos de prueba

    @GetMapping("/panel")
    public String mostrarPanelCliente(Model model) {
        // Obtener datos del cliente
        Cliente cliente = clienteService.getClienteById(CLIENTE_ID_FIJO)
                .orElseThrow(() -> new RuntimeException("Cliente de prueba no encontrado."));
        model.addAttribute("cliente", cliente);

        // Obtener pedidos del cliente
        List<Pedido> pedidos = pedidoService.getPedidosByClienteId(CLIENTE_ID_FIJO);
        model.addAttribute("pedidos", pedidos);

        // Obtener el último pedido (el más reciente)
        Pedido ultimoPedido = pedidos.stream()
                .max(Comparator.comparing(Pedido::getFecha))
                .orElse(null);
        model.addAttribute("ultimoPedido", ultimoPedido);

        return "cliente/panel"; // Nombre de la plantilla Thymeleaf
    }

    @PostMapping("/panel/actualizar-datos")
    public String actualizarDatosCliente(@RequestParam("nombre") String nombre,
                                         @RequestParam("telefono") String telefono,
                                         @RequestParam("cuit") String cuit,
                                         @RequestParam("direccion") String direccion,
                                         @RequestParam(value = "direccion2", required = false) String direccion2) {
        Cliente cliente = clienteService.getClienteById(CLIENTE_ID_FIJO)
                .orElseThrow(() -> new RuntimeException("Cliente de prueba no encontrado."));

        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setCuit(cuit);
        cliente.setDireccion(direccion);
        cliente.setDireccion2(direccion2);

        clienteService.saveCliente(cliente);

        return "redirect:/cliente/panel";
    }
}
