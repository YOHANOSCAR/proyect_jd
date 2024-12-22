package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.entity.*;
import com.jennyduarte.sis.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final DetalleTransaccionService detalleTransaccionService;
    private final ProductoService productoService;
    private final ContactoService contactoService;
    private final HistorialInteraccionService historialInteraccionService;
    private final UsuarioService usuarioService; // Inyección de UsuarioService

    public TransaccionController(TransaccionService transaccionService,
                                 DetalleTransaccionService detalleTransaccionService,
                                 ProductoService productoService,
                                 ContactoService contactoService,
                                 HistorialInteraccionService historialInteraccionService,
                                 UsuarioService usuarioService) { // Agregado aquí
        this.transaccionService = transaccionService;
        this.detalleTransaccionService = detalleTransaccionService;
        this.productoService = productoService;
        this.contactoService = contactoService;
        this.historialInteraccionService = historialInteraccionService;
        this.usuarioService = usuarioService; // Inicialización aquí
    }
    @GetMapping
    public String listarVentas(Model model) {
        model.addAttribute("ventas", transaccionService.listarTodos());
        return "ventas/lista";
    }

    @GetMapping("/crear")
    public String crearVentaForm(Model model) {
        Transaccion venta = new Transaccion();

        // Obtener el usuario autenticado como vendedor
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Obtener el nombre de usuario autenticado

        Usuario vendedor = usuarioService.obtenerPorUsername(username); // Buscar al vendedor
        venta.setVendedor(vendedor); // Asignar el vendedor autenticado a la transacción

        // Configurar valores para el formulario
        model.addAttribute("transaccion", venta);
        model.addAttribute("clientes", contactoService.listarClientes()); // Lista filtrada de clientes
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("tiposTransaccion", List.of(Transaccion.TipoTransaccion.VENTA.name(), Transaccion.TipoTransaccion.ALQUILER.name()));
        model.addAttribute("estadosTransaccion", List.of(Transaccion.EstadoTransaccion.PENDIENTE.name(), Transaccion.EstadoTransaccion.COMPLETADA.name(), Transaccion.EstadoTransaccion.CANCELADA.name()));

        return "ventas/formulario";
    }




    @PostMapping
    public String guardarVenta(@ModelAttribute Transaccion venta,
                               @RequestParam("productoId") List<Long> productoIds,
                               @RequestParam("cantidades") List<Integer> cantidades,
                               @RequestParam("precios") List<BigDecimal> precios) {

        // Calcular el total de la transacción
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < productoIds.size(); i++) {
            total = total.add(precios.get(i).multiply(BigDecimal.valueOf(cantidades.get(i))));
        }

        // Establecer detalles de la transacción
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(total);

        // Guardar transacción
        Transaccion transaccionGuardada = transaccionService.guardar(venta);

        // Crear detalles de la transacción
        for (int i = 0; i < productoIds.size(); i++) {
            Producto producto = productoService.obtenerPorId(productoIds.get(i));
            DetalleTransaccion detalle = DetalleTransaccion.builder()
                    .transaccion(transaccionGuardada)
                    .producto(producto)
                    .cantidad(cantidades.get(i))
                    .precioUnitario(precios.get(i))
                    .build();
            detalleTransaccionService.guardar(detalle);

            // Registrar historial de interacción
            HistorialInteraccion historial = HistorialInteraccion.builder()
                    .cliente(venta.getCliente())
                    .producto(producto)
                    .tipo(HistorialInteraccion.TipoInteraccion.COMPRA)
                    .fecha(LocalDateTime.now())
                    .build();
            historialInteraccionService.guardar(historial);
        }

        return "redirect:/ventas";
    }

    @GetMapping("/{id}")
    public String verDetallesVenta(@PathVariable Long id, Model model) {
        Transaccion transaccion = transaccionService.obtenerPorId(id);
        List<DetalleTransaccion> detalles = detalleTransaccionService.listarTodos()
                .stream()
                .filter(d -> d.getTransaccion().getId().equals(id))
                .toList();

        model.addAttribute("venta", transaccion);
        model.addAttribute("detalles", detalles);

        return "ventas/detalle";
    }
}
