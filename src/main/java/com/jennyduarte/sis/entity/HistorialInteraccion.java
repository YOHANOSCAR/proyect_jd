package com.jennyduarte.sis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "historial_interacciones")
public class HistorialInteraccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contacto_cliente_id", nullable = false)
    private Contacto cliente;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInteraccion tipo;

    @Column(nullable = false)
    private LocalDateTime fecha;
    public enum TipoInteraccion{
        CLIC_PROMO,CONSULTA,COMPRA,ALQUILER
    }
}
