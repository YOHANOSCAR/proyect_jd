package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.PromocionPersonalizada;
import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.entity.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PromocionRepository extends JpaRepository<PromocionPersonalizada, Long> {

    @Query("SELECT p FROM PromocionPersonalizada p WHERE p.producto = :producto AND p.cliente = :cliente")
    Optional<PromocionPersonalizada> findPromocionActiva(@Param("producto") Producto producto, @Param("cliente") Contacto cliente);
}
