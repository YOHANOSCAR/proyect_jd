package com.jennyduarte.sis.controllers;
import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.Usuario;
import com.jennyduarte.sis.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController extends BaseController<Usuario, Long> {

    public UsuarioController(UsuarioService usuarioService) {
        super(usuarioService, "usuarios");
    }

    @Override
    protected Usuario getNuevaEntidad() {
        return new Usuario();
    }
}

