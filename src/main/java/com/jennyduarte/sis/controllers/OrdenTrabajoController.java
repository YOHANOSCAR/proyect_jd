package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.OrdenTrabajo;
import com.jennyduarte.sis.service.OrdenTrabajoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ordenes")
public class OrdenTrabajoController extends BaseController<OrdenTrabajo, Long> {

    public OrdenTrabajoController(OrdenTrabajoService ordenTrabajoService) {
        super(ordenTrabajoService, "ordenes");
    }

    @Override
    protected OrdenTrabajo getNuevaEntidad() {
        return new OrdenTrabajo();
    }
}