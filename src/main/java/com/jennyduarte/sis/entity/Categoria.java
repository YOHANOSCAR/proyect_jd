package com.jennyduarte.sis.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NombreCategoria nombre; // Enum: Alta Gama, Fiesta, Novias

    private String descripcion;

    public enum NombreCategoria{
        ALTA_GAMA,FIESTA,NOVIAS
    }
}
