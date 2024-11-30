package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.DetalleTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleTransaccionRepository extends JpaRepository<DetalleTransaccion, Long> {
}
