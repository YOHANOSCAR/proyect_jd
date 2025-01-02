package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.service.DashboardService;
import com.jennyduarte.sis.service.ProductoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final ProductoService productoService;

    public DashboardController(DashboardService dashboardService,
                               ProductoService productoService) {
        this.dashboardService = dashboardService;
        this.productoService = productoService;
    }

    @GetMapping("/dashboard")
    public String verDashboard(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = auth.getName(); // Obtiene el nombre del usuario

        model.addAttribute("usuarioActual", usuarioActual);

        model.addAttribute("totalContactos", dashboardService.obtenerTotalContactos());
        model.addAttribute("contactosClientes", dashboardService.obtenerTotalContactosClientes());
        model.addAttribute("productosCount", dashboardService.obtenerTotalProductosDisponibles());
        model.addAttribute("totalProductosVendidos", dashboardService.obtenerTotalProductosVendidos());
        model.addAttribute("ventasCount", dashboardService.obtenerTotalVentasAcumuladas());
        model.addAttribute("alquilerCount", dashboardService.obtenerAlquileresRealizados());


        model.addAttribute("gananciasMensualesSum", dashboardService.obtenerGananciasMensualesSum());
        model.addAttribute("gananciasAnualesSum", dashboardService.obtenerGananciasAnualesSum());


        model.addAttribute("productos", productoService.listarTodos());


        model.addAttribute("cantidadesVendidasSemanal", dashboardService.obtenerCantidadesVendidasSemanal());
        model.addAttribute("gananciasSemanales", dashboardService.obtenerGananciasSemanales());
        model.addAttribute("ventasPorSemana", dashboardService.obtenerVentasPorSemana());
        // -- Cantidades de Productos Vendidos --
        model.addAttribute("cantidadesVendidasSemanal", dashboardService.obtenerCantidadesVendidasSemanal());
        model.addAttribute("cantidadesVendidasMensual", dashboardService.obtenerCantidadesVendidasMensual());
        model.addAttribute("cantidadesVendidasAnual", dashboardService.obtenerCantidadesVendidasAnual());


        model.addAttribute("gananciasSemanales", dashboardService.obtenerGananciasSemanales());
        model.addAttribute("gananciasMensuales", dashboardService.obtenerGananciasMensuales());
        model.addAttribute("gananciasAnuales", dashboardService.obtenerGananciasAnuales());


        model.addAttribute("ventasPorSemana", dashboardService.obtenerVentasPorSemana());

        return "dashboard";
    }
}
