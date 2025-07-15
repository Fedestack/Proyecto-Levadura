package com.fedestack.Levadura.controller;

import com.fedestack.Levadura.model.Cliente;
import com.fedestack.Levadura.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clientes") // Todas las URLs de este controlador empezarán con /clientes
public class ClienteController {

   @Autowired
   private ClienteService clienteService;

   // Mapeo para mostrar la lista de todos los clientes
   @GetMapping("")
   public String listarClientes(Model model) {
      List<Cliente> clientes = clienteService.getAllClientes();
      model.addAttribute("clientes", clientes);
      return "clientes/lista"; // Devuelve el nombre de la plantilla Thymeleaf
   }

   // Mapeo para mostrar el formulario de creación de un nuevo cliente
   @GetMapping("/nuevo")
   public String mostrarFormularioDeNuevoCliente(Model model) {
      model.addAttribute("cliente", new Cliente());
      return "clientes/formulario"; // Plantilla para crear/editar
   }

   // Mapeo para procesar el envío del formulario de nuevo cliente
   @PostMapping("/guardar")
   public String guardarCliente(@ModelAttribute("cliente") Cliente cliente) {
      clienteService.saveCliente(cliente);
      return "redirect:/clientes"; // Redirige a la lista de clientes
   }

   // Mapeo para mostrar el formulario de edición de un cliente existente
   @GetMapping("/editar/{id}")
   public String mostrarFormularioDeEditarCliente(@PathVariable Long id, Model model) {
      Cliente cliente = clienteService.getClienteById(id)
              .orElseThrow(() -> new IllegalArgumentException("ID de Cliente no válido:" + id));
      model.addAttribute("cliente", cliente);
      return "clientes/formulario";
   }

   // Mapeo para eliminar un cliente
   @GetMapping("/eliminar/{id}")
   public String eliminarCliente(@PathVariable Long id) {
      clienteService.deleteCliente(id);
      return "redirect:/clientes";
   }
}
