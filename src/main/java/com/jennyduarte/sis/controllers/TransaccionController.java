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
        // Crear una nueva transacción
        Transaccion transaccion = new Transaccion();

        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario vendedor = usuarioService.obtenerPorUsername(username);
        if (vendedor == null || vendedor.getContacto() == null) {
            model.addAttribute("error", "No se pudo encontrar el vendedor autenticado.");
            return "error";
        }

        // Configurar el vendedor en la transacción
        transaccion.setVendedor(vendedor);

        // Verificar y cargar listas necesarias
        List<Contacto> clientes = contactoService.listarClientes();
        List<Producto> productos = productoService.listarTodos();

        // Validar datos antes de enviar a la vista
        if (clientes.isEmpty() || productos.isEmpty()) {
            model.addAttribute("error", "No hay datos suficientes (clientes o productos) para crear una transacción.");
            return "error";
        }

        // Añadir datos al modelo
        model.addAttribute("transaccion", transaccion);
        model.addAttribute("clientes", clientes);
        model.addAttribute("productos", productos);
        model.addAttribute("vendedorNombre", vendedor.getContacto().getNombre());
        model.addAttribute("tiposTransaccion", List.of(Transaccion.TipoTransaccion.VENTA.name(), Transaccion.TipoTransaccion.ALQUILER.name()));
        model.addAttribute("metodosPago", List.of(Pago.MetodoPago.EFECTIVO.name(), Pago.MetodoPago.TARJETA.name(), Pago.MetodoPago.TRANSAFERENCIA.name()));

        return "transacciones/formulario";
    }

    @PostMapping
    public String guardarTransaccion(@RequestParam("clienteId") Long clienteId,
                                     @RequestParam("tipo") String tipoTransaccion,
                                     @RequestParam("metodoPago") String metodoPago,
                                     @RequestParam("notas") String notas,
                                     @RequestParam("productoIds") List<Long> productoIds,
                                     @RequestParam("cantidades") List<Integer> cantidades,
                                     @RequestParam("precios") List<BigDecimal> precios,
                                     @RequestParam("descuentos") List<BigDecimal> descuentos,
                                     Model model) {
        try {
            // 1. Vendedor autenticado
            Usuario vendedor = usuarioService.obtenerUsuarioAutenticado();
            if (vendedor == null) {
                model.addAttribute("error", "No se pudo identificar al vendedor autenticado.");
                return "error";
            }

            // 2. Cliente
            Contacto cliente = contactoService.obtenerPorId(clienteId);
            if (cliente == null) {
                model.addAttribute("error", "El cliente seleccionado no existe.");
                return "error";
            }

            // 3. Crear la transacción
            Transaccion.TipoTransaccion tipo = Transaccion.TipoTransaccion.valueOf(tipoTransaccion);
            Transaccion transaccion = Transaccion.builder()
                    .cliente(cliente)
                    .vendedor(vendedor)
                    .fecha(LocalDateTime.now())
                    .tipo(tipo)
                    .estado(Transaccion.EstadoTransaccion.PENDIENTE) // inicial
                    .notas(notas)
                    .build();

            // Aún no guardamos en DB para poder persistir detalles sin problemas
            transaccionService.guardar(transaccion);

            // 4. Procesar detalles
            BigDecimal totalCalculado = BigDecimal.ZERO;

            for (int i = 0; i < productoIds.size(); i++) {
                // a) Obtener producto
                Producto producto = productoService.obtenerPorId(productoIds.get(i));
                if (producto == null) {
                    model.addAttribute("error", "Producto inexistente con ID: " + productoIds.get(i));
                    return "error";
                }

                // b) Cantidad y descuento
                Integer cant = cantidades.get(i);
                BigDecimal desc = descuentos.get(i) != null ? descuentos.get(i) : BigDecimal.ZERO;

                // c) El precio que viene del front en "precios.get(i)" ya está ajustado
                //    (puede ser precioVenta o costoAlquiler). Pero por seguridad, vamos a
                //    recalcular en backend si lo prefieres:
                //      BigDecimal precioUnitario = (tipo == Transaccion.TipoTransaccion.VENTA)
                //          ? producto.getPrecioVenta()
                //          : producto.getCostoAlquiler();
                //    O si confiamos en el front, usamos "precios.get(i)".
                BigDecimal precioUnitario = precios.get(i);

                // d) Subtotal en backend
                //    Subtotal = (precioUnitario * cant) - (descuento% de ese total)
                //    OJO: tu método hace: precioUnitario * cant - (precioUnitario * desc/100)
                //         (esto en multiplicación con la cantidad).
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cant));
                BigDecimal descuentoValor = subtotal.multiply(desc).divide(BigDecimal.valueOf(100));
                BigDecimal subtotalFinal = subtotal.subtract(descuentoValor);

                // e) Crear el detalle
                DetalleTransaccion detalle = DetalleTransaccion.builder()
                        .transaccion(transaccion)
                        .producto(producto)
                        .cantidad(cant)
                        .precioUnitario(precioUnitario)
                        .descuento(desc)
                        .build();

                // f) Calcular subtotal (usando tu método transitorio)
                detalle.calcularSubtotal();
                // O, si lo prefieres, puedes asignarle la variable subtotalFinal:
                // detalle.setSubtotal(subtotalFinal);

                detalleTransaccionService.guardar(detalle);

                // g) Actualizar total
                totalCalculado = totalCalculado.add(subtotalFinal);

                // h) Descontar stock solo si es VENTA
                if (tipo == Transaccion.TipoTransaccion.VENTA) {
                    if (producto.getCantidadDisponible() < cant) {
                        model.addAttribute("error", "Stock insuficiente para el producto: " + producto.getNombre());
                        return "error";
                    }
                    producto.setCantidadDisponible(producto.getCantidadDisponible() - cant);
                    productoService.guardar(producto);
                }
            }

            // 5. Actualizar el total de la transacción
            transaccion.setTotal(totalCalculado);
            transaccionService.guardar(transaccion);

            // 6. Registrar pago (por ahora, 100% del total)
            Pago nuevoPago = Pago.builder()
                    .transaccion(transaccion)
                    .monto(totalCalculado)
                    .fechaPago(LocalDateTime.now())
                    .metodo(Pago.MetodoPago.valueOf(metodoPago))
                    .notas("Pago inicial")
                    .build();
            pagoService.guardar(nuevoPago);

            // 7. Actualizar 'pagado' en la transacción
            BigDecimal pagadoActual = transaccion.getPagado().add(totalCalculado);
            transaccion.setPagado(pagadoActual);

            // 8. Verificar si la transacción está completamente pagada
            if (pagadoActual.compareTo(transaccion.getTotal()) >= 0) {
                transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
            }
            transaccionService.guardar(transaccion);

            return "transacciones/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al procesar la transacción: " + e.getMessage());
            return "error";
        }
    }


    @GetMapping("/{id}")
    public String verDetallesTransaccion(@PathVariable Long id, Model model) {
        Transaccion transaccion = transaccionService.obtenerPorId(id);
        List<DetalleTransaccion> detalles = detalleTransaccionService.listarPorTransaccion(id);
        List<Pago> pagos = pagoService.listarPorTransaccion(id);

        if (transaccion == null) {
            model.addAttribute("error", "La transacción no existe.");
            return "error";
        }

        model.addAttribute("transaccion", transaccion);
        model.addAttribute("detalles", detalles);
        model.addAttribute("pagos", pagos);

        return "transacciones/detalle";
    }
}
