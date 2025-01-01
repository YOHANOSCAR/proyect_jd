package com.jennyduarte.sis.service;

import com.jennyduarte.sis.entity.MovimientoInventario;
import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.repository.MovimientoInventarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoInventarioRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    public MovimientoInventario registrarMovimiento(Producto producto,
                                                    MovimientoInventario.TipoMovimiento tipo,
                                                    Integer cantidad,
                                                    String motivo) {
        String usuario = obtenerUsuarioActual();

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .tipo(tipo)
                .cantidad(cantidad)
                .fecha(LocalDateTime.now())
                .motivo(motivo)
                .usuario(usuario) // Incluye el usuario autenticado
                .build();

        return movimientoInventarioRepository.save(movimiento);
    }

    // Listar todos los movimientos de inventario
    public Iterable<MovimientoInventario> listarTodos() {
        return movimientoInventarioRepository.findAll();
    }

    // Método para obtener el usuario autenticado
    private String obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Retorna el username del usuario autenticado
        }
        return "Usuario desconocido"; // Valor por defecto si no hay un usuario autenticado
    }
    public boolean existenMovimientosAsociados(Producto producto) {
        return movimientoInventarioRepository.existsByProducto(producto);
    }


}
