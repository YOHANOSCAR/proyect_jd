package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.Boceto;
import com.jennyduarte.sis.service.BocetoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bocetos")
public class BocetoController extends BaseController<Boceto, Long> {

    public BocetoController(BocetoService bocetoService) {
        super(bocetoService, "bocetos");
    }

    @Override
    protected Boceto getNuevaEntidad() {
        return new Boceto();
    }
}