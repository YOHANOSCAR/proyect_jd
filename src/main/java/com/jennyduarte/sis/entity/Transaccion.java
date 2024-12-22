package com.jennyduarte.sis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transacciones")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contacto_cliente_id", nullable = false)
    private Contacto cliente;

    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor; // Nuevo atributo para asociar al vendedor

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado = EstadoTransaccion.PENDIENTE;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private BigDecimal pagado = BigDecimal.ZERO;

    @Transient
    private BigDecimal saldo;

    private String notas;

    public enum TipoTransaccion {
        VENTA, ALQUILER
    }

    public enum EstadoTransaccion {
        PENDIENTE, COMPLETADA, CANCELADA
    }
}
