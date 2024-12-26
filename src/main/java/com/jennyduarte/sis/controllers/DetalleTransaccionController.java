package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.DetalleTransaccion;
import com.jennyduarte.sis.service.DetalleTransaccionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/detalles")
public class DetalleTransaccionController extends BaseController<DetalleTransaccion, Long> {

    public DetalleTransaccionController(DetalleTransaccionService detalleService) {
        super(detalleService, "detalles");
    }

    @Override
    protected DetalleTransaccion getNuevaEntidad() {
        return new DetalleTransaccion();
    }
}
