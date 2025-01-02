package com.jennyduarte.sis.repository;


import com.jennyduarte.sis.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}