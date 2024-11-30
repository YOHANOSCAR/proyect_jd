package com.jennyduarte.sis.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "recomendaciones_personalizadas")
public class RecomendacionPersonalizada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contacto_cliente_id", nullable = false)
    private Contacto cliente;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private LocalDateTime fechaRecomendacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRecomendacion estado = EstadoRecomendacion.PENDIENTE;
    public enum EstadoRecomendacion{
        PENDIENTE, ACEPTADA, RECHAZADA
    }
}
