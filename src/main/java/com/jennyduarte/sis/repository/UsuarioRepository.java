package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por username
    Optional<Usuario> findByUsername(String username);

    // Verificar existencia por username
    boolean existsByUsername(String username);

    // Buscar usuario por contacto_id
    Optional<Usuario> findByContactoId(Long contactoId);
}
