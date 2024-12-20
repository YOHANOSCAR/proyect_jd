package com.jennyduarte.sis.config;

import com.jennyduarte.sis.entity.Categoria;
import com.jennyduarte.sis.repository.CategoriaRepository;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
public class CategoriaConfig {

    private final CategoriaRepository categoriaRepository;

    public CategoriaConfig(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @PostConstruct
    public void cargarCategorias() {
        if (categoriaRepository.count() == 0) { // Solo inserta si la tabla está vacía
            Arrays.asList(Categoria.NombreCategoria.values()).forEach(nombre -> {
                Categoria categoria = Categoria.builder()
                        .nombre(nombre)
                        .descripcion("Descripción de " + nombre)
                        .build();
                categoriaRepository.save(categoria);
            });
        }
    }
}
