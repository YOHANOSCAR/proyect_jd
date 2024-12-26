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
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoTransaccion estado = EstadoTransaccion.PENDIENTE;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Aquí usamos @Builder.Default para que, si no le pasamos un valor a 'pagado'
     * al usar el builder, se mantenga BigDecimal.ZERO en vez de null.
     */
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal pagado = BigDecimal.ZERO;

    @OneToMany(mappedBy = "transaccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTransaccion> detalles;

    private String notas;

    public BigDecimal getSaldo() {
        return total.subtract(pagado);
    }

    public void calcularTotal() {
        if (detalles != null) {
            this.total = detalles.stream()
                    .map(DetalleTransaccion::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.total = BigDecimal.ZERO;
        }
    }

    public enum TipoTransaccion {
        VENTA, ALQUILER
    }

    public enum EstadoTransaccion {
        PENDIENTE, COMPLETADA, CANCELADA
    }
}
