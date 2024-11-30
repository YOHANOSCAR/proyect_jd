package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.HistorialInteraccion;
import com.jennyduarte.sis.service.HistorialInteraccionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/historial")
public class HistorialInteraccionController extends BaseController<HistorialInteraccion, Long> {

    public HistorialInteraccionController(HistorialInteraccionService historialInteraccionService) {
        super(historialInteraccionService, "historial");
    }

    @Override
    protected HistorialInteraccion getNuevaEntidad() {
        return new HistorialInteraccion();
    }
}