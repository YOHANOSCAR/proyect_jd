package com.jennyduarte.sis.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "contactos")
public class Contacto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String telefono;
    private String email;
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContacto tipo; // Enum: CLIENTE, PROVEEDOR, USUARIO

    private String notas;

    // Nuevos campos
    private String razonSocial;
    private String ruc;

    public enum TipoContacto{
        CLIENTE, PROVEEDOR, USUARIO
    }
}
