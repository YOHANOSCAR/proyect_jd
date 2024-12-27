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

    public TransaccionController(
            TransaccionService transaccionService,
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

    // ----------------------------------------------------
    // LISTAR TRANSACCIONES
    // ----------------------------------------------------
    @GetMapping
    public String listarTransacciones(Model model) {
        List<Transaccion> transacciones = transaccionService.listarTodos();
        model.addAttribute("transacciones", transacciones);
        return "transacciones/lista";
    }

    // ----------------------------------------------------
    // FORMULARIO PARA CREAR UNA NUEVA TRANSACCION
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // GUARDAR (POST) LA TRANSACCION
    // ----------------------------------------------------
    @PostMapping
    public String guardarTransaccion(@RequestParam("clienteId") Long clienteId,
                                     @RequestParam("tipo") String tipoTransaccion,
                                     @RequestParam("metodoPago") String metodoPago,
                                     @RequestParam("notas") String notas,
                                     // Estos nombres deben coincidir con "name" del input hidden en el formulario
                                     @RequestParam("productoIds") List<Long> productoIds,
                                     @RequestParam("cantidades") List<Integer> cantidades,
                                     @RequestParam("precios") List<BigDecimal> precios,
                                     @RequestParam("descuentos") List<BigDecimal> descuentos,

                                     // Nuevos campos que vienes del formulario
                                     @RequestParam("total") BigDecimal totalRecibido,
                                     @RequestParam("pagado") BigDecimal pagadoRecibido,

                                     Model model) {
        try {
            // 1. Verificar Vendedor Autenticado
            Usuario vendedor = usuarioService.obtenerUsuarioAutenticado();
            if (vendedor == null) {
                model.addAttribute("error", "No se pudo identificar al vendedor autenticado.");
                return "error";
            }

            // 2. Verificar Cliente
            Contacto cliente = contactoService.obtenerPorId(clienteId);
            if (cliente == null) {
                model.addAttribute("error", "El cliente seleccionado no existe.");
                return "error";
            }

            // 3. Crear la Transacción (insert inicial con total=0, pagado=0 si usas @Builder.Default)
            Transaccion transaccion = Transaccion.builder()
                    .cliente(cliente)
                    .vendedor(vendedor)
                    .fecha(LocalDateTime.now())
                    .tipo(Transaccion.TipoTransaccion.valueOf(tipoTransaccion))
                    .notas(notas)
                    .build();
            // Primer INSERT: total=0, pagado=0
            transaccionService.guardar(transaccion);

            // 4. Calcular el total basados en detalles
            BigDecimal totalCalculado = BigDecimal.ZERO;

            for (int i = 0; i < productoIds.size(); i++) {
                Producto producto = productoService.obtenerPorId(productoIds.get(i));
                if (producto == null) {
                    model.addAttribute("error", "Uno de los productos seleccionados no existe.");
                    return "error";
                }

                // Tomar cantidad, precio y descuento
                Integer cant = (cantidades.get(i) != null) ? cantidades.get(i) : 0;
                BigDecimal desc = (descuentos.get(i) != null) ? descuentos.get(i) : BigDecimal.ZERO;
                BigDecimal precioUnit = (precios.get(i) != null) ? precios.get(i) : BigDecimal.ZERO;

                // Subtotal bruto y con descuento
                BigDecimal subtotalBruto = precioUnit.multiply(BigDecimal.valueOf(cant));
                BigDecimal descValor = subtotalBruto.multiply(desc).divide(BigDecimal.valueOf(100));
                BigDecimal subtotalFinal = subtotalBruto.subtract(descValor);

                // Crear y guardar detalle
                DetalleTransaccion detalle = DetalleTransaccion.builder()
                        .transaccion(transaccion)
                        .producto(producto)
                        .cantidad(cant)
                        .precioUnitario(precioUnit)
                        .descuento(desc)
                        .build();
                detalle.calcularSubtotal();
                detalleTransaccionService.guardar(detalle);

                // Sumar al total
                totalCalculado = totalCalculado.add(subtotalFinal);

                // Descontar stock si es VENTA
                if (transaccion.getTipo() == Transaccion.TipoTransaccion.VENTA) {
                    if (producto.getCantidadDisponible() < cant) {
                        model.addAttribute("error", "Stock insuficiente para el producto: " + producto.getNombre());
                        return "error";
                    }
                    producto.setCantidadDisponible(producto.getCantidadDisponible() - cant);
                    productoService.guardar(producto);
                }
            }

            // -------------------------------------------------------------
            // 5. Decidir qué valor asignar a 'total'
            // -------------------------------------------------------------
            // Opción A) Usar el total calculado en backend
            transaccion.setTotal(totalCalculado);

            // Opción B) Si prefieres forzar el valor del formulario (totalRecibido),
            //    descomenta esta línea y comenta la anterior:
            // transaccion.setTotal(totalRecibido);

            transaccionService.guardar(transaccion);  // -> UPDATE con el total

            // -------------------------------------------------------------
            // 6. Asignar 'pagado' según el formulario
            //    (En tu caso, puede estar en 0 o null si no implementas pagos todavía)
            // -------------------------------------------------------------
            if (pagadoRecibido == null) {
                pagadoRecibido = BigDecimal.ZERO;
            }
            transaccion.setPagado(pagadoRecibido);

            // Podrías decidir que "pagado" no se updatea si no hay un pago real,
            // pero como lo solicitas, lo guardamos:
            if (pagadoRecibido.compareTo(BigDecimal.ZERO) > 0) {
                // Podrías crear un registro en pagos, si lo deseas:
            /*
            Pago pago = Pago.builder()
                    .transaccion(transaccion)
                    .fechaPago(LocalDateTime.now())
                    .monto(pagadoRecibido)
                    .metodo(Pago.MetodoPago.valueOf(metodoPago))
                    .build();
            pagoService.guardar(pago);
            */
            }

            // Si 'pagado' >= 'total' marcamos COMPLETADA
            if (pagadoRecibido.compareTo(transaccion.getTotal()) >= 0) {
                transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
            }

            // Guardamos la transacción con su nuevo pagado (y estado)
            transaccionService.guardar(transaccion);

            // 7. Redirigir a la lista
            return "redirect:/transacciones";

        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al procesar la transacción: " + e.getMessage());
            return "error";
        }
    }

    // ----------------------------------------------------
    // MOSTRAR DETALLES DE UNA TRANSACCION
    // ----------------------------------------------------
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

        // Podrías crear un objeto Pago vacío para enlazar en la vista
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

            // 1. Crear objeto Pago
            Pago pago = Pago.builder()
                    .transaccion(transaccion)
                    .monto(monto)
                    .fechaPago(LocalDateTime.now())
                    .metodo(Pago.MetodoPago.valueOf(metodo))
                    .notas(notas)
                    .build();

            // 2. Guardar pago en BD
            pagoService.guardar(pago);

            // 3. Actualizar 'pagado' en la transacción
            BigDecimal nuevoPagado = transaccion.getPagado().add(monto);
            transaccion.setPagado(nuevoPagado);

            // 4. Si pagado >= total => COMPLETADA
            if (nuevoPagado.compareTo(transaccion.getTotal()) >= 0) {
                transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
            }

            transaccionService.guardar(transaccion);

            // Redirigir a la vista de detalles de la transacción
            return "redirect:/transacciones/" + transaccionId;

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar pago: " + e.getMessage());
            return "error";
        }
    }
}
