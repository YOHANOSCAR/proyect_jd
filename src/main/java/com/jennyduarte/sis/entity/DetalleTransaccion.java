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
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    @Transient
    private BigDecimal subtotal;

    public void calcularSubtotal() {
        BigDecimal descuentoAplicado = precioUnitario.multiply(descuento.divide(BigDecimal.valueOf(100)));
        this.subtotal = precioUnitario.subtract(descuentoAplicado).multiply(BigDecimal.valueOf(cantidad));
    }

}
