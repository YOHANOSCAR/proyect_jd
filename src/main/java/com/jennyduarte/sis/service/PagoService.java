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

    public PagoService(PagoRepository pagoRepository) {
        super(pagoRepository);
        this.pagoRepository = pagoRepository;
    }

    public void registrarPago(Transaccion transaccion, BigDecimal monto, Pago.MetodoPago metodo) {
        Pago pago = Pago.builder()
                .transaccion(transaccion)
                .monto(monto)
                .fechaPago(LocalDateTime.now())
                .metodo(metodo)
                .build();
        guardar(pago);
    }

    public List<Pago> listarPorTransaccion(Long transaccionId) {
        return pagoRepository.findAll()
                .stream()
                .filter(pago -> pago.getTransaccion().getId().equals(transaccionId))
                .toList();
    }
}
