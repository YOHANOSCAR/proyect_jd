package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Boceto;
import com.jennyduarte.sis.repository.BocetoRepository;
import org.springframework.stereotype.Service;

@Service
public class BocetoService extends BaseService<Boceto,Long>{
    public BocetoService(BocetoRepository bocetoRepository){
        super(bocetoRepository);
    }
}
