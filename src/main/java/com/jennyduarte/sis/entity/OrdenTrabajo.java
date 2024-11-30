package com.jennyduarte.sis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ordenes_trabajo")
public class OrdenTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "boceto_id", nullable = false)
    private Boceto boceto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado = EstadoOrden.EN_DISENO;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private BigDecimal costoTotal;

    private String notas;
    public enum EstadoOrden{
        EN_DISENO, EN_CONFECCION, COMPLETADO
    }
}
