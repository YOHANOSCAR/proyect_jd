package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.entity.MovimientoInventario;
import com.jennyduarte.sis.service.MovimientoInventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movimientos")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoInventarioService) {
        this.movimientoInventarioService = movimientoInventarioService;
    }

    @GetMapping
    public String listarMovimientos(Model model) {
        model.addAttribute("movimientos", movimientoInventarioService.listarTodos());
        return "movimientos/lista";
    }
}
