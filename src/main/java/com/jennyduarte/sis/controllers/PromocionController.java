package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.PromocionPersonalizada;
import com.jennyduarte.sis.service.PromocionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/promociones")
public class PromocionController extends BaseController<PromocionPersonalizada, Long> {

    public PromocionController(PromocionService promocionService) {
        super(promocionService, "promociones");
    }

    @Override
    protected PromocionPersonalizada getNuevaEntidad() {
        return new PromocionPersonalizada();
    }
}