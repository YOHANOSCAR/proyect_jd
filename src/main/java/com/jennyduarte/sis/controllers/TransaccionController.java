package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.entity.*;
import com.jennyduarte.sis.service.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.jennyduarte.sis.service.ReportePdfService;


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
    private final ReportePdfService reportePdfService;

    public TransaccionController(
            TransaccionService transaccionService,
            DetalleTransaccionService detalleTransaccionService,
            ProductoService productoService,
            ContactoService contactoService,
            UsuarioService usuarioService,
            PagoService pagoService,
            ReportePdfService reportePdfService) {
        this.transaccionService = transaccionService;
        this.detalleTransaccionService = detalleTransaccionService;
        this.productoService = productoService;
        this.contactoService = contactoService;
        this.usuarioService = usuarioService;
        this.pagoService = pagoService;
        this.reportePdfService = reportePdfService;
    }

    @GetMapping
    public String listarTransacciones(Model model) {
        List<Transaccion> transacciones = transaccionService.listarTodos();
        model.addAttribute("transacciones", transacciones);
        return "transacciones/lista";
    }

    @GetMapping("/crear")
    public String crearTransaccionForm(Model model) {
        // Crear transacción vacía
        Transaccion transaccion = new Transaccion();

        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario vendedor = usuarioService.obtenerPorUsername(username);
        if (vendedor == null || vendedor.getContacto() == null) {
            model.addAttribute("error", "No se pudo encontrar el vendedor autenticado.");
            return "error";
        }

        transaccion.setVendedor(vendedor);

        List<Contacto> clientes = contactoService.listarClientes();
        List<Producto> productos = productoService.listarTodos();

        if (clientes.isEmpty() || productos.isEmpty()) {
            model.addAttribute("error", "No hay datos suficientes (clientes o productos) para crear una transacción.");
            return "error";
        }

        model.addAttribute("transaccion", transaccion);
        model.addAttribute("clientes", clientes);
        model.addAttribute("productos", productos);
        model.addAttribute("vendedorNombre", vendedor.getContacto().getNombre());
        model.addAttribute("tiposTransaccion", List.of(
                Transaccion.TipoTransaccion.VENTA.name(),
                Transaccion.TipoTransaccion.ALQUILER.name()
        ));
        model.addAttribute("metodosPago", List.of(
                Pago.MetodoPago.EFECTIVO.name(),
                Pago.MetodoPago.TARJETA.name(),
                Pago.MetodoPago.TRANSAFERENCIA.name()
        ));
        return "transacciones/formulario";
    }

    @PostMapping
    public String guardarTransaccion(@RequestParam("clienteId") Long clienteId,
                                     @RequestParam("tipo") String tipoTransaccion,
                                     @RequestParam("metodoPago") String metodoPago,
                                     @RequestParam("montoInicial") BigDecimal montoInicial, // <--- ABONO INICIAL
                                     @RequestParam("notas") String notas,
                                     @RequestParam("productoIds") List<Long> productoIds,
                                     @RequestParam("cantidades") List<Integer> cantidades,
                                     @RequestParam("precios") List<BigDecimal> precios,
                                     @RequestParam("descuentos") List<BigDecimal> descuentos,
                                     @RequestParam("total") BigDecimal totalRecibido,
                                     @RequestParam("pagado") BigDecimal pagadoRecibido,
                                     Model model) {
        try {
            Usuario vendedor = usuarioService.obtenerUsuarioAutenticado();
            if (vendedor == null) {
                model.addAttribute("error", "No se pudo identificar al vendedor autenticado.");
                return "error";
            }



            Contacto cliente = contactoService.obtenerPorId(clienteId);
            if (cliente == null) {
                model.addAttribute("error", "El cliente seleccionado no existe.");
                return "error";
            }

            Transaccion transaccion = Transaccion.builder()
                    .cliente(cliente)
                    .vendedor(vendedor)
                    .fecha(LocalDateTime.now())
                    .tipo(Transaccion.TipoTransaccion.valueOf(tipoTransaccion))
                    .notas(notas)
                    .build();

            transaccionService.guardarOActualizar(transaccion);

            BigDecimal totalCalculado = BigDecimal.ZERO;
            for (int i = 0; i < productoIds.size(); i++) {
                Producto producto = productoService.obtenerPorId(productoIds.get(i));
                if (producto == null) {
                    model.addAttribute("error", "Producto inexistente con ID: " + productoIds.get(i));
                    return "error";
                }

                int cant = cantidades.get(i);
                BigDecimal desc = (descuentos.get(i) != null) ? descuentos.get(i) : BigDecimal.ZERO;
                BigDecimal precioUnit = (precios.get(i) != null) ? precios.get(i) : BigDecimal.ZERO;

                BigDecimal subtotal = precioUnit.multiply(BigDecimal.valueOf(cant));
                BigDecimal descuentoVal = subtotal.multiply(desc).divide(BigDecimal.valueOf(100));
                BigDecimal subtotalFinal = subtotal.subtract(descuentoVal);

                DetalleTransaccion detalle = DetalleTransaccion.builder()
                        .transaccion(transaccion)
                        .producto(producto)
                        .cantidad(cant)
                        .precioUnitario(precioUnit)
                        .descuento(desc)
                        .build();
                detalle.calcularSubtotal();
                detalleTransaccionService.guardar(detalle);

                totalCalculado = totalCalculado.add(subtotalFinal);

                if (transaccion.getTipo() == Transaccion.TipoTransaccion.VENTA) {
                    if (producto.getCantidadDisponible() < cant) {
                        model.addAttribute("error", "Stock insuficiente para: " + producto.getNombre());
                        return "error";
                    }
                    producto.setCantidadDisponible(producto.getCantidadDisponible() - cant);
                    productoService.guardar(producto);
                }
            }

            BigDecimal totalFinal = (totalRecibido != null) ? totalRecibido : totalCalculado;
            transaccion.setTotal(totalFinal);

            BigDecimal montoInicialActual = (montoInicial != null) ? montoInicial : BigDecimal.ZERO;
            transaccion.setPagado(montoInicialActual);

            if (transaccion.getPagado().compareTo(transaccion.getTotal()) > 0) {
                transaccion.setPagado(transaccion.getTotal());
                model.addAttribute("mensaje", "El abono ingresado excedía el total. Se ajustó automáticamente.");
            }


            if (transaccion.getPagado().compareTo(transaccion.getTotal()) >= 0) {
                transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
            } else {
                transaccion.setEstado(Transaccion.EstadoTransaccion.PENDIENTE);
            }

            transaccionService.guardarOActualizar(transaccion);
            Long transaccionId = transaccion.getId();


            if (montoInicialActual.compareTo(BigDecimal.ZERO) > 0) {
                Pago pagoInicial = Pago.builder()
                        .transaccion(transaccion)
                        .monto(montoInicialActual)
                        .fechaPago(LocalDateTime.now())
                        .metodo(Pago.MetodoPago.valueOf(metodoPago))
                        .notas("Abono inicial")
                        .build();
                pagoService.guardar(pagoInicial);
            }

            // 10. Redireccionar a la lista (o donde corresponda)
            return "redirect:/transacciones/" + transaccionId + "/boleta";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al procesar la transacción: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    public String verDetallesTransaccion(@PathVariable Long id, Model model) {
        Transaccion transaccion = transaccionService.obtenerPorId(id);
        if (transaccion == null) {
            model.addAttribute("error", "La transacción no existe.");
            return "error";
        }
        List<DetalleTransaccion> detalles = detalleTransaccionService.listarPorTransaccion(id);
        List<Pago> pagos = pagoService.listarPorTransaccion(id);

        model.addAttribute("transaccion", transaccion);
        model.addAttribute("detalles", detalles);
        model.addAttribute("pagos", pagos);

        return "transacciones/detalle";
    }
    @GetMapping("/{id}/nuevo-pago")
    public String mostrarFormularioPago(@PathVariable("id") Long id, Model model) {
        Transaccion transaccion = transaccionService.obtenerPorId(id);
        if (transaccion == null) {
            model.addAttribute("error", "La transacción no existe.");
            return "error";
        }
        Pago nuevoPago = new Pago();
        nuevoPago.setTransaccion(transaccion);  // Asociar la transacción

        model.addAttribute("transaccion", transaccion);
        model.addAttribute("nuevoPago", nuevoPago);

        // Lista de métodos de pago
        model.addAttribute("metodosPago", List.of(
                Pago.MetodoPago.EFECTIVO.name(),
                Pago.MetodoPago.TARJETA.name(),
                Pago.MetodoPago.TRANSAFERENCIA.name()
        ));

        return "transacciones/pagoForm"; // vista Thymeleaf
    }
    @PostMapping("/{id}/nuevo-pago")
    public String registrarPago(@PathVariable("id") Long transaccionId,
                                @RequestParam("monto") BigDecimal monto,
                                @RequestParam("metodo") String metodo,
                                @RequestParam(value="notas", required=false) String notas,
                                Model model) {
        try {
            Transaccion transaccion = transaccionService.obtenerPorId(transaccionId);
            if (transaccion == null) {
                model.addAttribute("error", "Transacción no encontrada.");
                return "error";
            }

            Pago pago = Pago.builder()
                    .transaccion(transaccion)
                    .monto(monto)
                    .fechaPago(LocalDateTime.now())
                    .metodo(Pago.MetodoPago.valueOf(metodo))
                    .notas(notas)
                    .build();

            pagoService.guardar(pago);
            BigDecimal nuevoPagado = transaccion.getPagado().add(monto);
            transaccion.setPagado(nuevoPagado);
            if (nuevoPagado.compareTo(transaccion.getTotal()) >= 0) {
                transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
            }
            transaccionService.guardar(transaccion);

            return "redirect:/transacciones/" + transaccionId;

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar pago: " + e.getMessage());
            return "error";
        }
    }
    @GetMapping("/{id}/reporte")
    public ResponseEntity<byte[]> generarReporte(@PathVariable Long id) {
        try {
            byte[] pdfBytes = reportePdfService.generarReporteTransaccion(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            headers.setContentDisposition(ContentDisposition.inline().filename("reporte-transaccion.pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @GetMapping("/{id}/boleta")
    public ResponseEntity<byte[]> generarBoleta(@PathVariable Long id) {
        try {
            byte[] pdfBytes = reportePdfService.generarBoleta(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename("boleta-transaccion-" + id + ".pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}
