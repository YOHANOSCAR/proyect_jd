package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
