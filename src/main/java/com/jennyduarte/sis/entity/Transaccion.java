package com.jennyduarte.sis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private Usuario vendedor;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo; // VENTA, ALQUILER

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoTransaccion estado = EstadoTransaccion.PENDIENTE;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
        private BigDecimal pagado = BigDecimal.ZERO;

    @OneToMany(mappedBy = "transaccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTransaccion> detalles;

    private String notas;

    public BigDecimal getSaldo() {
        return total.subtract(pagado);
    }

    public enum TipoTransaccion {
        VENTA, ALQUILER,
    }

    public enum EstadoTransaccion {
        PENDIENTE, COMPLETADA, CANCELADA
    }

    // Método opcional para recalcular el total en base a sus detalles
    public void calcularTotal() {
        if (detalles != null && !detalles.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (DetalleTransaccion det : detalles) {
                det.calcularSubtotal();
                sum = sum.add(det.getSubtotal());
            }
            this.total = sum;
        } else {
            this.total = BigDecimal.ZERO;
        }
    }
}
