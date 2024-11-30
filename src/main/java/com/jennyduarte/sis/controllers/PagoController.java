package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.Pago;
import com.jennyduarte.sis.service.PagoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pagos")
public class PagoController extends BaseController<Pago, Long> {

    public PagoController(PagoService pagoService) {
        super(pagoService, "pagos");
    }

    @Override
    protected Pago getNuevaEntidad() {
        return new Pago();
    }
}