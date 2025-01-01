package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.MovimientoInventario;
import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.exception.RecursoNoEncontradoException;
import com.jennyduarte.sis.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioService movimientoInventarioService;

    public ProductoService(ProductoRepository productoRepository,
                           MovimientoInventarioService movimientoInventarioService) {
        this.productoRepository = productoRepository;
        this.movimientoInventarioService = movimientoInventarioService;
    }

    // Listar todos los productos
    public List<Producto> listarTodos() {
        List<Producto> productos = productoRepository.findAll();
        if (productos.isEmpty()) {
            return List.of();
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
        boolean esNuevo = producto.getId() == null;
        Producto guardado = productoRepository.save(producto);

        movimientoInventarioService.registrarMovimiento(
                guardado,
                esNuevo ? MovimientoInventario.TipoMovimiento.ENTRADA : MovimientoInventario.TipoMovimiento.AJUSTE,
                producto.getCantidadDisponible(),
                esNuevo ? "Creación de producto" : "Edición de producto"
        );

        return guardado;
    }

    // Eliminar un producto
    public void eliminar(Long id) {
        Producto producto = obtenerPorId(id);

        // Verificar si el producto tiene movimientos asociados
        boolean tieneReferencias = movimientoInventarioService.existenMovimientosAsociados(producto);
        if (tieneReferencias) {
            throw new IllegalStateException("No se puede eliminar el producto porque tiene movimientos asociados.");
        }

        // Registrar movimiento de salida antes de eliminar
        movimientoInventarioService.registrarMovimiento(
                producto,
                MovimientoInventario.TipoMovimiento.SALIDA,
                producto.getCantidadDisponible(),
                "Eliminación de producto"
        );

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
