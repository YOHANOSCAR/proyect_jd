package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository extends JpaRepository <Transaccion, Long> {
}
