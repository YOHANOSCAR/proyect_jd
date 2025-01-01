package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.entity.Contacto;
import com.jennyduarte.sis.service.ContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/contactos")
public class ContactoController {

    private final ContactoService contactoService;

    @Autowired
    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    // Mostrar la lista de contactos
    @GetMapping
    public String listarContactos(Model model) {
        model.addAttribute("entidades", contactoService.listarTodos());
        return "contactos/lista";
    }

    // Mostrar formulario de creación
    @GetMapping("/crear")
    public String crearFormulario(Model model) {
        model.addAttribute("entidad", new Contacto());
        return "contactos/formulario";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Contacto contacto = contactoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de contacto inválido: " + id));
        model.addAttribute("entidad", contacto);
        return "contactos/formulario";
    }

    // Guardar contacto (crear o actualizar)
    @PostMapping
    public String guardarContacto(@ModelAttribute("entidad") Contacto contacto,
                                  BindingResult result,
                                  Model model) {
        // Validaciones adicionales

        // Validar Nombre
        if (contacto.getNombre() == null || contacto.getNombre().trim().isEmpty()) {
            result.rejectValue("nombre", "error.contacto", "El nombre es obligatorio.");
        }

        // Validar Teléfono
        if (contacto.getTelefono() != null && !contacto.getTelefono().trim().isEmpty()) {
            if (!contacto.getTelefono().matches("\\d{9}")) {
                result.rejectValue("telefono", "error.contacto", "Teléfono debe contener exactamente 9 dígitos numéricos.");
            }
        }

        // Validar RUC
        if (contacto.getRuc() != null && !contacto.getRuc().trim().isEmpty()) {
            if (!contacto.getRuc().matches("\\d{11}")) {
                result.rejectValue("ruc", "error.contacto", "RUC debe contener exactamente 11 dígitos numéricos.");
            }
        }

        // Si hay errores, retornar al formulario
        if (result.hasErrors()) {
            return "contactos/formulario";
        }

        // Guardar el contacto (crear o actualizar)
        contactoService.guardar(contacto);

        // Redirigir a la lista de contactos
        return "redirect:/contactos";
    }

    // Eliminar contacto
    @GetMapping("/eliminar/{id}")
    public String eliminarContacto(@PathVariable Long id) {
        contactoService.eliminarPorId(id);
        return "redirect:/contactos";
    }
}
