package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.Temporada;
import com.jennyduarte.sis.service.TemporadaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/temporadas")
public class TemporadaController extends BaseController<Temporada, Long> {

    public TemporadaController(TemporadaService temporadaService) {
        super(temporadaService, "temporadas");
    }

    @Override
    protected Temporada getNuevaEntidad() {
        return new Temporada();
    }
}
