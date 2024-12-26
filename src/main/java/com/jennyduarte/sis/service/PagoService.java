package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Pago;
import com.jennyduarte.sis.entity.Transaccion;
import com.jennyduarte.sis.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class PagoService extends BaseService<Pago, Long> {

    private final PagoRepository pagoRepository;
    private final TransaccionService transaccionService;

    public PagoService(PagoRepository pagoRepository, TransaccionService transaccionService) {
        super(pagoRepository);
        this.pagoRepository = pagoRepository;
        this.transaccionService = transaccionService;
    }

    public Pago registrarPago(Transaccion transaccion, BigDecimal monto, Pago.MetodoPago metodo, String notas) {
        // 1. Crear el pago
        Pago pago = Pago.builder()
                .transaccion(transaccion)
                .monto(monto)
                .fechaPago(LocalDateTime.now())
                .metodo(metodo)
                .notas(notas)
                .build();

        // 2. Guardar
        Pago saved = this.guardar(pago);

        // 3. Actualizar la transacción
        transaccion.setPagado(transaccion.getPagado().add(monto));
        if (transaccion.getPagado().compareTo(transaccion.getTotal()) >= 0) {
            transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
        }
        transaccionService.guardar(transaccion);

        return saved;
    }

    public List<Pago> listarPorTransaccion(Long transaccionId) {
        return pagoRepository.findAll().stream()
                .filter(pago -> pago.getTransaccion().getId().equals(transaccionId))
                .toList();
    }
}
