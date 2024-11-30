package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Temporada;
import com.jennyduarte.sis.repository.TemporadaRepository;
import org.springframework.stereotype.Service;

@Service
public class TemporadaService extends BaseService<Temporada, Long>{
    public TemporadaService(TemporadaRepository temporadaRepository){
        super(temporadaRepository);
    }
}
