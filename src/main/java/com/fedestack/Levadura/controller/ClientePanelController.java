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
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.util.Map;

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

        // Obtener pedidos del cliente y ordenarlos por fecha de forma descendente
        List<Pedido> pedidos = pedidoService.getPedidosByClienteId(CLIENTE_ID_FIJO);
        pedidos.sort(Comparator.comparing(Pedido::getFecha).reversed());
        model.addAttribute("pedidos", pedidos);

        // Obtener el último pedido (el más reciente)
        Pedido ultimoPedido = pedidos.stream()
                .max(Comparator.comparing(Pedido::getFecha))
                .orElse(null);
        model.addAttribute("ultimoPedido", ultimoPedido);

        // Calcular gastos mensuales
        Map<String, BigDecimal> monthlySpending = calculateMonthlySpending(pedidos, 6); // Últimos 6 meses
        model.addAttribute("spendingLabels", monthlySpending.keySet());
        model.addAttribute("spendingValues", monthlySpending.values());

        return "cliente/panel"; // Nombre de la plantilla Thymeleaf
    }

    private Map<String, BigDecimal> calculateMonthlySpending(List<Pedido> pedidos, int monthsToInclude) {
        Map<YearMonth, BigDecimal> monthlyTotals = new LinkedHashMap<>();
        YearMonth currentMonth = YearMonth.now();

        // Inicializar los últimos 'monthsToInclude' meses con 0
        for (int i = monthsToInclude - 1; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            monthlyTotals.put(month, BigDecimal.ZERO);
        }

        // Sumar los totales de los pedidos a los meses correspondientes
        for (Pedido pedido : pedidos) {
            YearMonth pedidoMonth = YearMonth.from(pedido.getFecha());
            if (monthlyTotals.containsKey(pedidoMonth)) {
                monthlyTotals.computeIfPresent(pedidoMonth, (key, val) -> val.add(pedido.getTotal()));
            }
        }

        // Convertir a un mapa de String (nombre del mes) a BigDecimal
        Map<String, BigDecimal> formattedMonthlySpending = new LinkedHashMap<>();
        for (Map.Entry<YearMonth, BigDecimal> entry : monthlyTotals.entrySet()) {
            String monthName = entry.getKey().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            formattedMonthlySpending.put(monthName, entry.getValue());
        }
        return formattedMonthlySpending;
    }

    @PostMapping("/panel/actualizar-datos")
    public String actualizarDatosCliente(@RequestParam("nombre") String nombre,
                                         @RequestParam("nombreLocal") String nombreLocal,
                                         @RequestParam("telefono") String telefono,
                                         @RequestParam("cuit") String cuit,
                                         @RequestParam("direccion") String direccion,
                                         @RequestParam(value = "direccion2", required = false) String direccion2) {
        Cliente cliente = clienteService.getClienteById(CLIENTE_ID_FIJO)
                .orElseThrow(() -> new RuntimeException("Cliente de prueba no encontrado."));

        cliente.setNombre(nombre);
        cliente.setNombreLocal(nombreLocal);
        cliente.setTelefono(telefono);
        cliente.setCuit(cuit);
        cliente.setDireccion(direccion);
        cliente.setDireccion2(direccion2);

        clienteService.saveCliente(cliente);

        return "redirect:/cliente/panel";
    }
}
