package com.jennyduarte.sis.controllers.base;

import com.jennyduarte.sis.service.BaseService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<T, ID> {
    private final BaseService<T, ID> service;
    private final String basePath;

    protected BaseController(BaseService<T, ID> service, String basePath) {
        this.service = service;
        this.basePath = basePath;
    }

    @GetMapping
    public String listarTodos(Model model) {
        List<T> entidades = service.listarTodos();
        model.addAttribute("entidades", entidades);
        return basePath + "/lista";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("entidad", getNuevaEntidad());
        return basePath + "/formulario";
    }

    @PostMapping
    public String guardar(@ModelAttribute T entidad) {
        service.guardar(entidad);
        return "redirect:/" + basePath;
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable ID id, Model model) {
        T entidad = service.obtenerPorId(id);
        model.addAttribute("entidad", entidad);
        return basePath + "/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable ID id) {
        service.eliminar(id);
        return "redirect:/" + basePath;
    }

    protected abstract T getNuevaEntidad();
}

