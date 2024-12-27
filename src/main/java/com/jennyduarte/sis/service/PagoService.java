package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Pago;
import com.jennyduarte.sis.entity.Transaccion;
import com.jennyduarte.sis.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void registrarPago(Transaccion transaccion, BigDecimal monto, Pago.MetodoPago metodo) {
        // 1. Crear y guardar el Pago
        Pago pago = Pago.builder()
                .transaccion(transaccion)
                .monto(monto)
                .fechaPago(LocalDateTime.now())
                .metodo(metodo)
                .build();
        this.guardar(pago);

        // 2. Actualizar "pagado" de la transaccion
        BigDecimal nuevoPagado = transaccion.getPagado().add(monto);
        transaccion.setPagado(nuevoPagado);
        // 3. Cambiar estado si pagado >= total
        if (nuevoPagado.compareTo(transaccion.getTotal()) >= 0) {
            transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
        }
        transaccionService.guardarOActualizar(transaccion);
    }

    public List<Pago> listarPorTransaccion(Long transaccionId) {
        return pagoRepository.findAll()
                .stream()
                .filter(p -> p.getTransaccion().getId().equals(transaccionId))
                .toList();
    }
}
