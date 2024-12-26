package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    // Método adicional para verificar existencia por username
    boolean existsByUsername(String username);
}
