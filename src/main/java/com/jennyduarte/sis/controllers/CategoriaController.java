package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.Categoria;
import com.jennyduarte.sis.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categorias")
public class CategoriaController extends BaseController<Categoria, Long> {

    public CategoriaController(CategoriaService categoriaService) {
        super(categoriaService, "categorias");
    }

    @Override
    protected Categoria getNuevaEntidad() {
        return new Categoria();
    }
}