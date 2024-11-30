package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.RecomendacionPersonalizada;
import com.jennyduarte.sis.repository.RecomendacionRepository;
import org.springframework.stereotype.Service;

@Service
public class RecomendacionService extends BaseService<RecomendacionPersonalizada, Long> {
    public RecomendacionService(RecomendacionRepository recomendacionRepository) {
        super(recomendacionRepository);
    }
}
