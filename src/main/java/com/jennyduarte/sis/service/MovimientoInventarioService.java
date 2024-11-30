package com.jennyduarte.sis.service;


import com.jennyduarte.sis.entity.MovimientoInventario;
import com.jennyduarte.sis.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;

@Service
public class MovimientoInventarioService extends BaseService<MovimientoInventario, Long> {
    public MovimientoInventarioService(MovimientoInventarioRepository movimientoInventarioRepository) {
        super(movimientoInventarioRepository);
    }
}