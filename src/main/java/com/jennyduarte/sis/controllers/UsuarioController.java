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
        model.addAttribute("contactos", contactoService.listarUsuarios()); // Filtrar contactos
        return "usuarios/formulario";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String editarForm(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("contactos", contactoService.listarUsuarios());
        return "usuarios/formulario"; // Retorna el formulario con los datos del usuario
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String guardar(@ModelAttribute Usuario usuario, Model model) {
        try {
            if (usuario.getId() == null) {
                usuarioService.guardarUsuario(usuario);
            } else {
                usuarioService.actualizarUsuario(usuario);
            }
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("contactos", contactoService.listarUsuarios());
            return "usuarios/formulario";
        }
    }



    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String eliminar(@PathVariable Long id, Model model) {
        try {
            usuarioService.eliminarUsuario(id);
        } catch (Exception e) {
            model.addAttribute("mensajeError", "No se puede eliminar el usuario porque está asociado a otros procesos.");
            model.addAttribute("usuarios", usuarioService.listarTodos());
            return "usuarios/lista";
        }

        return "redirect:/usuarios";
    }

}
