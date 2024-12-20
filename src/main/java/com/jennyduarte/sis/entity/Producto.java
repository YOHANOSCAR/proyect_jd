package com.jennyduarte.sis.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoProducto; // Código único del producto

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "contacto_proveedor_id")
    private Contacto proveedor;

    @ManyToOne
    @JoinColumn(name = "temporada_id")
    private Temporada temporada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProducto tipo; // Enum: venta, alquiler, personalizado

    @Enumerated(EnumType.STRING)
    private EstadoProducto estado = EstadoProducto.DISPONIBLE;

    private BigDecimal precioVenta;
    private BigDecimal costoAlquiler;
    private Integer cantidadDisponible;

    @Column(nullable = true)
    private String imagenUrl; // URL de la imagen del producto

    public enum TipoProducto{
        venta, alquiler, personalizado
    }
    public enum EstadoProducto{
        VENTA,ALQUILER, DISPONIBLE, PERZONALIZADO
    }
}
