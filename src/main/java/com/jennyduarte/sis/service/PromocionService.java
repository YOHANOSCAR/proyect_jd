package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.PromocionPersonalizada;
import com.jennyduarte.sis.repository.PromocionRepository;
import org.springframework.stereotype.Service;

@Service
public class PromocionService extends BaseService<PromocionPersonalizada, Long>{
    public PromocionService(PromocionRepository promocionRepository){
        super(promocionRepository);
    }
}
