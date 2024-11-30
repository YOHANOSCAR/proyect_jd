package com.jennyduarte.sis.service;
import com.jennyduarte.sis.entity.Usuario;
import com.jennyduarte.sis.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends BaseService<Usuario, Long> {
    public UsuarioService(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
    }
}

