package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Pago;
import com.jennyduarte.sis.repository.PagoRepository;
import org.springframework.stereotype.Service;

@Service
public class PagoService extends BaseService<Pago, Long> {
    public PagoService(PagoRepository pagoRepository) {
        super(pagoRepository);
    }
}