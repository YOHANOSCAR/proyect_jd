package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.exception.RecursoNoEncontradoException;
import com.jennyduarte.sis.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Listar todos los productos
    public List<Producto> listarTodos() {
        List<Producto> productos = productoRepository.findAll();
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay productos disponibles.");
        }
        return productos;
    }

    // Obtener un producto por ID
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID " + id + " no encontrado."));
    }

    // Guardar o actualizar un producto
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    // Eliminar un producto
    public void eliminar(Long id) {
        Producto producto = obtenerPorId(id);
        productoRepository.delete(producto);
    }
}
