package com.jennyduarte.sis.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class DashboardService {

    // Ejemplo: obtener cantidades vendidas diarias (simulado)
    public Map<String, Integer> obtenerCantidadesVendidasDiario() {
        return Map.of("Producto A", 15, "Producto B", 25, "Producto C", 10);
    }

    public Map<String, Integer> obtenerCantidadesVendidasSemanal() {
        return Map.of("Producto A", 70, "Producto B", 50, "Producto C", 30);
    }

    public Map<String, Integer> obtenerCantidadesVendidasMensual() {
        return Map.of("Producto A", 300, "Producto B", 200, "Producto C", 150);
    }

    public Map<String, Double> obtenerGananciasDiarias() {
        return Map.of("Producto A", 150.0, "Producto B", 250.0, "Producto C", 100.0);
    }

    public Map<String, Double> obtenerGananciasSemanales() {
        return Map.of("Producto A", 700.0, "Producto B", 500.0, "Producto C", 300.0);
    }

    public Map<String, Double> obtenerGananciasMensuales() {
        return Map.of("Producto A", 3000.0, "Producto B", 2000.0, "Producto C", 1500.0);
    }

    public Map<String, Double> obtenerVentasPorSemana() {
        return Map.of("Semana 1", 1000.0, "Semana 2", 1200.0, "Semana 3", 900.0, "Semana 4", 1500.0);
    }
}
