// TransaccionService.java
package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Transaccion;
import com.jennyduarte.sis.repository.TransaccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransaccionService extends BaseService<Transaccion, Long> {
    private final TransaccionRepository transaccionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository) {
        super(transaccionRepository);
        this.transaccionRepository = transaccionRepository;
    }

    public List<Transaccion> listarPorEstado(Transaccion.EstadoTransaccion estado) {
        return transaccionRepository.findAll()
                .stream()
                .filter(transaccion -> transaccion.getEstado() == estado)
                .toList();
    }

    public void actualizar(Transaccion transaccion) {
        guardar(transaccion);
    }
}
