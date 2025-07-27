package com.fedestack.Levadura.controller;

import com.fedestack.Levadura.model.Cliente;
import com.fedestack.Levadura.model.MetodoPago;
import com.fedestack.Levadura.service.ClienteService;
import com.fedestack.Levadura.service.MetodoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cliente/metodos-pago")
public class MetodoPagoController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private MetodoPagoService metodoPagoService;

    // ID de cliente fijo para pruebas
    private static final Long CLIENTE_ID_FIJO = 1L; // Puedes cambiar este ID según tus datos de prueba

    @GetMapping
    public String mostrarMetodosPago(Model model) {
        Cliente cliente = clienteService.getClienteById(CLIENTE_ID_FIJO)
                .orElseThrow(() -> new RuntimeException("Cliente de prueba no encontrado."));
        model.addAttribute("cliente", cliente);
        model.addAttribute("metodosPago", metodoPagoService.getMetodosPagoByClienteId(CLIENTE_ID_FIJO));
        model.addAttribute("nuevoMetodoPago", new MetodoPago()); // Para el formulario de añadir
        return "cliente/metodos_pago";
    }

    @PostMapping("/guardar")
    public String guardarMetodoPago(@ModelAttribute("nuevoMetodoPago") MetodoPago metodoPago) {
        Cliente cliente = clienteService.getClienteById(CLIENTE_ID_FIJO)
                .orElseThrow(() -> new RuntimeException("Cliente de prueba no encontrado."));
        metodoPago.setCliente(cliente);
        metodoPagoService.saveMetodoPago(metodoPago);
        return "redirect:/cliente/metodos-pago";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMetodoPago(@PathVariable Long id) {
        metodoPagoService.deleteMetodoPago(id);
        return "redirect:/cliente/metodos-pago";
    }
}
