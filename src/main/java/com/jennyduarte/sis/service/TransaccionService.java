package com.jennyduarte.sis.service;
import com.jennyduarte.sis.entity.Transaccion;
import com.jennyduarte.sis.repository.TransaccionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransaccionService extends BaseService<Transaccion, Long> {
    public TransaccionService(TransaccionRepository transaccionRepository) {
        super(transaccionRepository);
    }
}
