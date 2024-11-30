package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.RecomendacionPersonalizada;
import com.jennyduarte.sis.service.RecomendacionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recomendaciones")
public class RecomendacionController extends BaseController<RecomendacionPersonalizada, Long> {

    public RecomendacionController(RecomendacionService recomendacionService) {
        super(recomendacionService, "recomendaciones");
    }

    @Override
    protected RecomendacionPersonalizada getNuevaEntidad() {
        return new RecomendacionPersonalizada();
    }
}