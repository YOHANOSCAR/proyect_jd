package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.exception.RecursoNoEncontradoException;
import com.jennyduarte.sis.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            return List.of(); // Retorna una lista vacía
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

    // Filtrar productos por proveedor
    public List<Producto> listarPorProveedor(Long proveedorId) {
        return productoRepository.findAll().stream()
                .filter(producto -> producto.getProveedor() != null && producto.getProveedor().getId().equals(proveedorId))
                .collect(Collectors.toList());
    }

    // Filtrar productos por categoría
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findAll().stream()
                .filter(producto -> producto.getCategoria() != null && producto.getCategoria().getId().equals(categoriaId))
                .collect(Collectors.toList());
    }
}
