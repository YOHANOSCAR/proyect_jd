package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
