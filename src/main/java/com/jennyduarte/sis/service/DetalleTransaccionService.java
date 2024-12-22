package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.DetalleTransaccion;
import com.jennyduarte.sis.repository.DetalleTransaccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleTransaccionService extends BaseService<DetalleTransaccion, Long> {
    public DetalleTransaccionService(DetalleTransaccionRepository detalleRepository) {
        super(detalleRepository);
    }

}