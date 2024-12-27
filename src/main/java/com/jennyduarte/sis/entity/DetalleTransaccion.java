package com.jennyduarte.sis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "detalle_transacciones")
public class DetalleTransaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaccion_id", nullable = false)
    private Transaccion transaccion;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = true)
    private BigDecimal descuento = BigDecimal.ZERO;  // Se asume % de descuento

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    @Transient
    private BigDecimal subtotal;

    public void calcularSubtotal() {
        // descuento es un porcentaje, e.g. 10 -> 10%
        BigDecimal descuentoValor = precioUnitario
                .multiply(descuento)
                .divide(BigDecimal.valueOf(100));
        BigDecimal precioConDescuento = precioUnitario.subtract(descuentoValor);
        this.subtotal = precioConDescuento.multiply(BigDecimal.valueOf(cantidad));
    }
}
