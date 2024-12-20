package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.controllers.base.BaseController;
import com.jennyduarte.sis.entity.Usuario;
import com.jennyduarte.sis.service.ContactoService;
import com.jennyduarte.sis.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ContactoService contactoService;

    public UsuarioController(UsuarioService usuarioService, ContactoService contactoService) {
        this.usuarioService = usuarioService;
        this.contactoService = contactoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String listarTodos(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String crearForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("contactos", contactoService.listarTodos());
        return "usuarios/formulario";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.guardarUsuario(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String editarForm(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("contactos", contactoService.listarTodos());
        return "usuarios/formulario";
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }
}
