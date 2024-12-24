package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Contacto;
import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.entity.PromocionPersonalizada;
import com.jennyduarte.sis.repository.PromocionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PromocionService extends BaseService<PromocionPersonalizada, Long> {

    private final PromocionRepository promocionRepository;

    public PromocionService(PromocionRepository promocionRepository) {
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
    }

    public BigDecimal calcularDescuento(Producto producto, Contacto cliente) {
        Optional<PromocionPersonalizada> promocion = promocionRepository.findPromocionActiva(producto, cliente);
        return promocion.map(p -> producto.getPrecioVenta().multiply(p.getDescuento().divide(BigDecimal.valueOf(100))))
                .orElse(BigDecimal.ZERO);
    }
}
