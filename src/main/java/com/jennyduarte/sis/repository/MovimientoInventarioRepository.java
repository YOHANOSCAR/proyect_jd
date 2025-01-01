package com.jennyduarte.sis.repository;

import com.jennyduarte.sis.entity.MovimientoInventario;
import com.jennyduarte.sis.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    boolean existsByProducto(Producto producto);

}
