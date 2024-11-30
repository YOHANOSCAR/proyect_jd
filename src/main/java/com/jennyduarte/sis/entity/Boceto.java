package com.jennyduarte.sis.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bocetos")
public class Boceto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contacto_cliente_id", nullable = false)
    private Contacto cliente;

    private String evento;

    private BigDecimal busto;
    private BigDecimal cintura;
    private BigDecimal cadera;
    private BigDecimal talleA;
    private BigDecimal talleB;
    private BigDecimal alturaTotal;
    private BigDecimal largoDeseado;
    private BigDecimal espalda;
    private BigDecimal anchoPuño;
    private BigDecimal contornoBrazo;

    private String notas;
}
