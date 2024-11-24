package com.jennyduarte.sis.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private double precio;

    private int cantidadDisponible;

    @Enumerated(EnumType.STRING)
    private TipoProducto tipo;
    private String imagen; // Campo para almacenar la URL de la imagen

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaUltimaModificacion;

    public enum TipoProducto {
        VENTA,
        ALQUILER,
        PERSONALIZADO
    }
}