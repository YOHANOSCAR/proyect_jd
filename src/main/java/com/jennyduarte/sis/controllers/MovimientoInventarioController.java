package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.MovimientoInventario;
import com.jennyduarte.sis.service.MovimientoInventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movimientos")
public class MovimientoInventarioController extends BaseController<MovimientoInventario, Long> {

    public MovimientoInventarioController(MovimientoInventarioService movimientoInventarioService) {
        super(movimientoInventarioService, "movimientos");
    }

    @Override
    protected MovimientoInventario getNuevaEntidad() {
        return new MovimientoInventario();
    }
}