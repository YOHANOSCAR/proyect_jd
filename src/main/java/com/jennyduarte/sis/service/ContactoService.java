package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Contacto;
import com.jennyduarte.sis.repository.ContactoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactoService extends BaseService<Contacto, Long> {

    public ContactoService(ContactoRepository contactoRepository) {
        super(contactoRepository);
    }
    // Método para listar solo los contactos que son proveedores
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

}
