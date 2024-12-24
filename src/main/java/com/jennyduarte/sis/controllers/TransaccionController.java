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
@RequestMapping("/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final DetalleTransaccionService detalleTransaccionService;
    private final ProductoService productoService;
    private final ContactoService contactoService;
    private final UsuarioService usuarioService;
    private final PagoService pagoService;

    public TransaccionController(TransaccionService transaccionService,
                                 DetalleTransaccionService detalleTransaccionService,
                                 ProductoService productoService,
                                 ContactoService contactoService,
                                 UsuarioService usuarioService,
                                 PagoService pagoService) {
        this.transaccionService = transaccionService;
        this.detalleTransaccionService = detalleTransaccionService;
        this.productoService = productoService;
        this.contactoService = contactoService;
        this.usuarioService = usuarioService;
        this.pagoService = pagoService;
    }

    @GetMapping
    public String listarTransacciones(Model model) {
        List<Transaccion> transacciones = transaccionService.listarTodos();
        model.addAttribute("transacciones", transacciones);
        return "transacciones/lista";
    }

    @GetMapping("/crear")
    public String crearTransaccionForm(Model model) {
        Transaccion transaccion = new Transaccion();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario vendedor = usuarioService.obtenerPorUsername(username);
        transaccion.setVendedor(vendedor);

        model.addAttribute("transaccion", transaccion);
        model.addAttribute("clientes", contactoService.listarClientes());
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("tiposTransaccion", List.of(Transaccion.TipoTransaccion.VENTA.name(), Transaccion.TipoTransaccion.ALQUILER.name()));
        model.addAttribute("metodosPago", List.of(Pago.MetodoPago.EFECTIVO.name(), Pago.MetodoPago.TARJETA.name(), Pago.MetodoPago.TRANSAFERENCIA.name()));

        return "transacciones/formulario";
    }

    @PostMapping
    public String guardarTransaccion(@ModelAttribute Transaccion transaccion,
                                     @RequestParam("productoId") List<Long> productoIds,
                                     @RequestParam("cantidades") List<Integer> cantidades,
                                     @RequestParam("metodoPago") String metodoPago) {

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < productoIds.size(); i++) {
            Producto producto = productoService.obtenerPorId(productoIds.get(i));
            BigDecimal precio = transaccion.getTipo() == Transaccion.TipoTransaccion.VENTA
                    ? producto.getPrecioVenta()
                    : producto.getCostoAlquiler();
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidades.get(i)));

            total = total.add(subtotal);

            DetalleTransaccion detalle = DetalleTransaccion.builder()
                    .transaccion(transaccion)
                    .producto(producto)
                    .cantidad(cantidades.get(i))
                    .precioUnitario(precio)
                    .build();

            detalle.calcularSubtotal();
            detalleTransaccionService.guardar(detalle);
        }

        transaccion.setFecha(LocalDateTime.now());
        transaccion.setTotal(total);
        transaccionService.guardar(transaccion);

        Pago pago = Pago.builder()
                .transaccion(transaccion)
                .monto(total)
                .fechaPago(LocalDateTime.now())
                .metodo(Pago.MetodoPago.valueOf(metodoPago))
                .build();
        pagoService.guardar(pago);

        return "redirect:/transacciones";
    }

    @GetMapping("/{id}")
    public String verDetallesTransaccion(@PathVariable Long id, Model model) {
        Transaccion transaccion = transaccionService.obtenerPorId(id);
        List<DetalleTransaccion> detalles = detalleTransaccionService.listarPorTransaccion(id);
        List<Pago> pagos = pagoService.listarPorTransaccion(id);

        model.addAttribute("transaccion", transaccion);
        model.addAttribute("detalles", detalles);
        model.addAttribute("pagos", pagos);

        return "transacciones/detalle";
    }
}
