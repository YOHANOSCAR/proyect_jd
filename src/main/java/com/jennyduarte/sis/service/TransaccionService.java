package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Transaccion;
import com.jennyduarte.sis.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Guarda o actualiza una transacción en la base de datos.
     *
     * @param transaccion Transacción a guardar o actualizar.
     */
    @Transactional
    public void guardarOActualizar(Transaccion transaccion) {
        if (transaccion.getId() != null) {
            Transaccion transaccionExistente = transaccionRepository.findById(transaccion.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada con ID: " + transaccion.getId()));
            // Actualizar los campos necesarios
            transaccionExistente.setEstado(transaccion.getEstado());
            transaccionExistente.setTotal(transaccion.getTotal());
            transaccionExistente.setFecha(transaccion.getFecha());
            transaccionExistente.setVendedor(transaccion.getVendedor());
            transaccionRepository.save(transaccionExistente);
        } else {
            transaccionRepository.save(transaccion);
        }
    }
}
