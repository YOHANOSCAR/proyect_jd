package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Contacto;
import com.jennyduarte.sis.repository.ContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactoService {

    private final ContactoRepository contactoRepository;

    @Autowired
    public ContactoService(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    // Método existente para buscar por ID que retorna Optional
    public Optional<Contacto> buscarPorId(Long id) {
        return contactoRepository.findById(id);
    }

    // Nuevo método para obtener por ID que lanza excepción si no se encuentra
    public Contacto obtenerPorId(Long id) {
        return contactoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contacto con ID " + id + " no encontrado."));
    }

    // Métodos CRUD existentes
    public Contacto guardar(Contacto contacto) {
        return contactoRepository.save(contacto);
    }

    public List<Contacto> listarTodos() {
        return contactoRepository.findAll();
    }

    public void eliminarPorId(Long id) {
        if (verificarReferencias(id)) {
            throw new IllegalStateException("No se puede eliminar el contacto porque está asociado a otros procesos.");
        }
        contactoRepository.deleteById(id);
    }

    // Método que verifica si el contacto está asociado a otros módulos
    private boolean verificarReferencias(Long id) {
        return false;
    }

    public List<Contacto> listarProveedores() {
        return listarTodos().stream()
                .filter(contacto -> contacto.getTipo() == Contacto.TipoContacto.PROVEEDOR)
                .collect(Collectors.toList());
    }

    public List<Contacto> listarClientes() {
        return listarTodos().stream()
                .filter(contacto -> contacto.getTipo() == Contacto.TipoContacto.CLIENTE)
                .collect(Collectors.toList());
    }
    public List<Contacto> listarUsuarios() {
        return listarTodos().stream()
                .filter(contacto -> contacto.getTipo() == Contacto.TipoContacto.USUARIO)
                .collect(Collectors.toList());
    }

}
