package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.PromocionPersonalizada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromocionRepository extends JpaRepository<PromocionPersonalizada, Long> {
}
