package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {

}