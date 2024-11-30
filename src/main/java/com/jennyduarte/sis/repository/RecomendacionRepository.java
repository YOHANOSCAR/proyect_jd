package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.RecomendacionPersonalizada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecomendacionRepository extends JpaRepository<RecomendacionPersonalizada, Long> {
}
