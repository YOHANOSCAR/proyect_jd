package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.DetalleTransaccion;
import com.jennyduarte.sis.repository.DetalleTransaccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleTransaccionService extends BaseService<DetalleTransaccion, Long> {
    private final DetalleTransaccionRepository detalleRepository;

    public DetalleTransaccionService(DetalleTransaccionRepository detalleRepository) {
        super(detalleRepository);
        this.detalleRepository = detalleRepository;
    }

    public List<DetalleTransaccion> listarPorTransaccion(Long transaccionId) {
        return detalleRepository.findAll()
                .stream()
                .filter(d -> d.getTransaccion().getId().equals(transaccionId))
                .toList();
    }
    public List<DetalleTransaccion> findAll() {
        return detalleRepository.findAll();
    }
}
