package com.jennyduarte.sis.service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseService<T, ID> {
    private final JpaRepository<T, ID> repository;

    protected BaseService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    public List<T> listarTodos() {
        return repository.findAll();
    }

    public T obtenerPorId(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso con ID " + id + " no encontrado."));
    }
    @Transactional
    public T guardar(T entidad) {
        return repository.save(entidad);
    }

    public void eliminar(ID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Recurso con ID " + id + " no encontrado.");
        }
        repository.deleteById(id);
    }
}

