package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.service.DashboardService;
import com.jennyduarte.sis.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final ProductoService productoService;

    public DashboardController(DashboardService dashboardService, ProductoService productoService) {
        this.dashboardService = dashboardService;
        this.productoService = productoService;
    }

    @GetMapping({"/", "/dashboard"})
    public String mostrarDashboard(Model model) {
        // Datos para las gráficas
        model.addAttribute("cantidadesVendidasDiario", dashboardService.obtenerCantidadesVendidasDiario());
        model.addAttribute("cantidadesVendidasSemanal", dashboardService.obtenerCantidadesVendidasSemanal());
        model.addAttribute("cantidadesVendidasMensual", dashboardService.obtenerCantidadesVendidasMensual());

        model.addAttribute("gananciasDiario", dashboardService.obtenerGananciasDiarias());
        model.addAttribute("gananciasSemanal", dashboardService.obtenerGananciasSemanales());
        model.addAttribute("gananciasMensual", dashboardService.obtenerGananciasMensuales());

        model.addAttribute("ventasSemana", dashboardService.obtenerVentasPorSemana());

        // Agregamos el usuario actual (en un caso real, obtén esto de la sesión o servicio de autenticación)
        model.addAttribute("usuarioActual", "Juan Pérez");

        // Obtenemos la lista de productos desde la base de datos
        List<Producto> productos = productoService.listarTodos();
        model.addAttribute("productos", productos);

        return "dashboard";
    }
}
