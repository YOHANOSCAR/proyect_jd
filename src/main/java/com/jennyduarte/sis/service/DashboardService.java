package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.DetalleTransaccion;
import com.jennyduarte.sis.entity.Transaccion;
import com.jennyduarte.sis.entity.Transaccion.TipoTransaccion;
import com.jennyduarte.sis.entity.Transaccion.EstadoTransaccion;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final TransaccionService transaccionService;
    private final DetalleTransaccionService detalleTransaccionService;
    private final ContactoService contactoService;
    private final ProductoService productoService;

    public DashboardService(TransaccionService transaccionService,
                            DetalleTransaccionService detalleTransaccionService,
                            ContactoService contactoService,
                            ProductoService productoService) {
        this.transaccionService = transaccionService;
        this.detalleTransaccionService = detalleTransaccionService;
        this.contactoService = contactoService;
        this.productoService = productoService;
    }

    // --------------------------------------------------------------------
    // TOTAL DE CONTACTOS REGISTRADOS
    // --------------------------------------------------------------------
    public int obtenerTotalContactos() {
        return contactoService.listarTodos().size();
    }

    // Clasificación de contactos por tipo; ajusta según tus tipos reales
    public int obtenerTotalContactosClientes() {
        return contactoService.listarClientes().size();
    }
    public int obtenerTotalContactosProveedores() {
        return contactoService.listarProveedores().size();
    }
    public int obtenerTotalContactosUsuarios() {
        return contactoService.listarUsuarios().size();
    }

    // --------------------------------------------------------------------
    // TOTAL DE PRODUCTOS (DISPONIBLES) Y TOTAL DE PRODUCTOS VENDIDOS
    // --------------------------------------------------------------------
    public int obtenerTotalProductosDisponibles() {
        return productoService.listarTodos().size();
    }

    public long obtenerTotalProductosVendidos() {
        // Para productos "vendidos" sumamos la cantidad de DetalleTransaccion
        // en transacciones de tipo VENTA y (opcional) estado COMPLETADA
        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        return detalles.stream()
                .filter(d -> d.getTransaccion().getTipo() == TipoTransaccion.VENTA)
                .filter(d -> d.getTransaccion().getEstado() == EstadoTransaccion.COMPLETADA)
                .mapToLong(DetalleTransaccion::getCantidad)
                .sum();
    }

    // --------------------------------------------------------------------
    // TOTAL DE VENTAS ACUMULADAS
    // --------------------------------------------------------------------
    public BigDecimal obtenerTotalVentasAcumuladas() {
        List<Transaccion> transacciones = transaccionService.findAll();
        return transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.VENTA)
                .filter(t -> t.getEstado() == EstadoTransaccion.COMPLETADA)
                .map(Transaccion::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --------------------------------------------------------------------
    // ALQUILERES REALIZADOS (EJEMPLO DE OTRA CATEGORÍA)
    // --------------------------------------------------------------------
    public long obtenerAlquileresRealizados() {
        List<Transaccion> transacciones = transaccionService.findAll();
        return transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.ALQUILER)
                .count();
    }

    // --------------------------------------------------------------------
    // CANTIDAD DE PRODUCTOS VENDIDOS DIARIO / SEMANAL / MENSUAL
    // (Retorna Map<String, Integer> => "NombreProducto" vs "CantidadVendida")
    // --------------------------------------------------------------------
    public Map<String, Integer> obtenerCantidadesVendidasDiario() {
        LocalDate hoy = LocalDate.now();
        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        Map<String, Integer> resultado = new HashMap<>();

        for (DetalleTransaccion dt : detalles) {
            Transaccion t = dt.getTransaccion();
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                LocalDate fecha = t.getFecha().toLocalDate();
                if (fecha.isEqual(hoy)) {
                    String nombre = dt.getProducto().getNombre();
                    int acumulado = resultado.getOrDefault(nombre, 0);
                    resultado.put(nombre, acumulado + dt.getCantidad());
                }
            }
        }
        return resultado;
    }

    public Map<String, Integer> obtenerCantidadesVendidasSemanal() {
        // Semana actual: Monday -> Sunday
        LocalDate inicioSemana = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate finSemana = inicioSemana.plusDays(6);

        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        Map<String, Integer> resultado = new HashMap<>();

        for (DetalleTransaccion dt : detalles) {
            Transaccion t = dt.getTransaccion();
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                LocalDate fecha = t.getFecha().toLocalDate();
                if (!fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana)) {
                    String nombre = dt.getProducto().getNombre();
                    int acumulado = resultado.getOrDefault(nombre, 0);
                    resultado.put(nombre, acumulado + dt.getCantidad());
                }
            }
        }
        return resultado;
    }

    public Map<String, Integer> obtenerCantidadesVendidasMensual() {
        // Mes actual
        LocalDate ahora = LocalDate.now();
        int mesActual = ahora.getMonthValue();
        int anioActual = ahora.getYear();

        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        Map<String, Integer> resultado = new HashMap<>();

        for (DetalleTransaccion dt : detalles) {
            Transaccion t = dt.getTransaccion();
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                LocalDate fecha = t.getFecha().toLocalDate();
                if (fecha.getMonthValue() == mesActual && fecha.getYear() == anioActual) {
                    String nombre = dt.getProducto().getNombre();
                    int acumulado = resultado.getOrDefault(nombre, 0);
                    resultado.put(nombre, acumulado + dt.getCantidad());
                }
            }
        }
        return resultado;
    }

    // --------------------------------------------------------------------
    // GANANCIAS DIARIAS / SEMANALES / MENSUALES
    // (Retorna Map<String, Double> => "NombreProducto" vs "Ganancia")
    // Ejemplo de cálculo de ganancia: subtotal (cantidad * precioUnitario)
    // menos el descuento. Ajustar según tu lógica de negocio.
    // --------------------------------------------------------------------
    public Map<String, Double> obtenerGananciasDiarias() {
        LocalDate hoy = LocalDate.now();
        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        Map<String, Double> resultado = new HashMap<>();

        for (DetalleTransaccion dt : detalles) {
            Transaccion t = dt.getTransaccion();
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                LocalDate fecha = t.getFecha().toLocalDate();
                if (fecha.isEqual(hoy)) {
                    String nombre = dt.getProducto().getNombre();
                    BigDecimal subtotal = dt.getPrecioUnitario()
                            .multiply(BigDecimal.valueOf(dt.getCantidad()));
                    BigDecimal descuento = subtotal
                            .multiply(dt.getDescuento().divide(BigDecimal.valueOf(100)));
                    BigDecimal ganancia = subtotal.subtract(descuento);

                    double acumulado = resultado.getOrDefault(nombre, 0.0);
                    resultado.put(nombre, acumulado + ganancia.doubleValue());
                }
            }
        }
        return resultado;
    }

    public Map<String, Double> obtenerGananciasSemanales() {
        LocalDate inicioSemana = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate finSemana = inicioSemana.plusDays(6);

        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        Map<String, Double> resultado = new HashMap<>();

        for (DetalleTransaccion dt : detalles) {
            Transaccion t = dt.getTransaccion();
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                LocalDate fecha = t.getFecha().toLocalDate();
                if (!fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana)) {
                    String nombre = dt.getProducto().getNombre();
                    BigDecimal subtotal = dt.getPrecioUnitario()
                            .multiply(BigDecimal.valueOf(dt.getCantidad()));
                    BigDecimal descuento = subtotal
                            .multiply(dt.getDescuento().divide(BigDecimal.valueOf(100)));
                    BigDecimal ganancia = subtotal.subtract(descuento);

                    double acumulado = resultado.getOrDefault(nombre, 0.0);
                    resultado.put(nombre, acumulado + ganancia.doubleValue());
                }
            }
        }
        return resultado;
    }

    public Map<String, Double> obtenerGananciasMensuales() {
        LocalDate ahora = LocalDate.now();
        int mesActual = ahora.getMonthValue();
        int anioActual = ahora.getYear();

        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        Map<String, Double> resultado = new HashMap<>();

        for (DetalleTransaccion dt : detalles) {
            Transaccion t = dt.getTransaccion();
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                LocalDate fecha = t.getFecha().toLocalDate();
                if (fecha.getMonthValue() == mesActual && fecha.getYear() == anioActual) {
                    String nombre = dt.getProducto().getNombre();
                    BigDecimal subtotal = dt.getPrecioUnitario()
                            .multiply(BigDecimal.valueOf(dt.getCantidad()));
                    BigDecimal descuento = subtotal
                            .multiply(dt.getDescuento().divide(BigDecimal.valueOf(100)));
                    BigDecimal ganancia = subtotal.subtract(descuento);

                    double acumulado = resultado.getOrDefault(nombre, 0.0);
                    resultado.put(nombre, acumulado + ganancia.doubleValue());
                }
            }
        }
        return resultado;
    }

    // --------------------------------------------------------------------
    // VENTAS POR SEMANA (IMPORTE TOTAL) - VENTAS POR MES
    // (Retorna Map<String, Double>)
    // --------------------------------------------------------------------
    public Map<String, Double> obtenerVentasPorSemana() {
        // Agrupamos por "semana del año"
        List<Transaccion> ventas = transaccionService.findAll();
        Map<String, Double> resultado = new HashMap<>();

        for (Transaccion t : ventas) {
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                int semana = t.getFecha()
                        .atZone(ZoneId.systemDefault())
                        .get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                String key = "Semana " + semana;

                double acumulado = resultado.getOrDefault(key, 0.0);
                double totalVenta = t.getTotal().doubleValue();
                resultado.put(key, acumulado + totalVenta);
            }
        }
        return resultado;
    }

    public Map<String, Double> obtenerVentasPorMes() {
        // Agrupamos por "número de mes"
        List<Transaccion> ventas = transaccionService.findAll();
        Map<String, Double> resultado = new HashMap<>();

        for (Transaccion t : ventas) {
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                int mes = t.getFecha().getMonthValue();
                String key = "Mes " + mes;

                double acumulado = resultado.getOrDefault(key, 0.0);
                double totalVenta = t.getTotal().doubleValue();
                resultado.put(key, acumulado + totalVenta);
            }
        }
        return resultado;
    }
    // CANTIDADES ANUALES
    public Map<String, Integer> obtenerCantidadesVendidasAnual() {
        LocalDate ahora = LocalDate.now();
        int anioActual = ahora.getYear();

        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        Map<String, Integer> resultado = new HashMap<>();

        for (DetalleTransaccion dt : detalles) {
            Transaccion t = dt.getTransaccion();
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                LocalDate fecha = t.getFecha().toLocalDate();
                if (fecha.getYear() == anioActual) {
                    String nombre = dt.getProducto().getNombre();
                    int acumulado = resultado.getOrDefault(nombre, 0);
                    resultado.put(nombre, acumulado + dt.getCantidad());
                }
            }
        }
        return resultado;
    }

    // GANANCIAS ANUALES
    public Map<String, Double> obtenerGananciasAnuales() {
        LocalDate ahora = LocalDate.now();
        int anioActual = ahora.getYear();

        List<DetalleTransaccion> detalles = detalleTransaccionService.findAll();
        Map<String, Double> resultado = new HashMap<>();

        for (DetalleTransaccion dt : detalles) {
            Transaccion t = dt.getTransaccion();
            if (t.getTipo() == TipoTransaccion.VENTA && t.getEstado() == EstadoTransaccion.COMPLETADA) {
                LocalDate fecha = t.getFecha().toLocalDate();
                if (fecha.getYear() == anioActual) {
                    String nombre = dt.getProducto().getNombre();
                    BigDecimal subtotal = dt.getPrecioUnitario().multiply(BigDecimal.valueOf(dt.getCantidad()));
                    BigDecimal descuento = subtotal.multiply(dt.getDescuento().divide(BigDecimal.valueOf(100)));
                    BigDecimal ganancia = subtotal.subtract(descuento);

                    double acumulado = resultado.getOrDefault(nombre, 0.0);
                    resultado.put(nombre, acumulado + ganancia.doubleValue());
                }
            }
        }
        return resultado;
    }
    public BigDecimal obtenerGananciasMensualesSum() {
        Map<String, Double> gananciasMap = obtenerGananciasMensuales();
        double total = gananciasMap.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        return BigDecimal.valueOf(total);
    }
    public BigDecimal obtenerGananciasAnualesSum() {
        Map<String, Double> gananciasMap = obtenerGananciasAnuales();
        double total = gananciasMap.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        return BigDecimal.valueOf(total);
    }



}
