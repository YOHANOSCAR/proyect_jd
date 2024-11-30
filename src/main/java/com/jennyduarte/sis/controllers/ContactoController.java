package com.jennyduarte.sis.controllers;
import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.Contacto;
import com.jennyduarte.sis.service.ContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contactos")
public class ContactoController extends BaseController<Contacto, Long> {

    public ContactoController(ContactoService contactoService) {
        super(contactoService, "contactos");
    }

    @Override
    protected Contacto getNuevaEntidad() {
        return new Contacto();
    }
}
