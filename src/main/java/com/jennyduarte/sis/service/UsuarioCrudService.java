package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Usuario;
import com.jennyduarte.sis.repository.UsuarioRepository;
import org.springframework.stereotype.Service;



@Service
public class UsuarioCrudService extends BaseService<Usuario, Long> {

    public UsuarioCrudService(UsuarioRepository usuarioRepository) {
        super(usuarioRepository); // Llama al constructor de BaseService
    }

}


