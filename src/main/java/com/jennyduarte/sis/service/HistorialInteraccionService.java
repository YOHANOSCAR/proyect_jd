package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.HistorialInteraccion;
import com.jennyduarte.sis.repository.HistorialInteraccionRepository;
import org.springframework.stereotype.Service;

@Service
public class HistorialInteraccionService extends BaseService<HistorialInteraccion, Long> {
    public HistorialInteraccionService(HistorialInteraccionRepository historialRepository) {
        super(historialRepository);
    }
}
