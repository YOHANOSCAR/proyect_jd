package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.OrdenTrabajo;
import com.jennyduarte.sis.repository.OrdenTrabajoRepository;
import org.springframework.stereotype.Service;

@Service
public class OrdenTrabajoService extends BaseService<OrdenTrabajo, Long> {
    public OrdenTrabajoService(OrdenTrabajoRepository ordenTrabajoRepository) {
        super(ordenTrabajoRepository);
    }
}