package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Categoria;
import com.jennyduarte.sis.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService extends BaseService<Categoria, Long> {
    public CategoriaService(CategoriaRepository categoriaRepository){
        super(categoriaRepository);
    }
}
