package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 40)
    private NombreCategoria nombre;

    @Column(nullable = false)
    @Builder.Default
    private Boolean oficial = true;

    /* ──────────────────────────────────────────────────────────────────── */
    public enum NombreCategoria {
        COMIDA_RAPIDA("Comida rápida"),
        TRADICIONAL("Tradicional"),
        FUSION("Fusión"),
        ITALIANO("Italiano"),
        CHINO("Chino"),
        JAPONES("Japonés"),
        INDIO("Indio"),
        MEXICANO("Mexicano"),
        VEGANO("Vegano"),
        VEGETARIANO("Vegetariano"),
        MEDITERRANEO("Mediterráneo"),
        AMERICANO("Americano"), 
        ESPANOL("Español"),
        ARABE("Árabe"),
        TURCO("Turco"),
        MARISQUERIA("Marisquería"),
        BRASERIA("Brasería");
    
        private final String serialName;
    
        NombreCategoria(String serialName) {
            this.serialName = serialName;
        }

        public String getSerialName() {
            return serialName;
        }
    }

    public String getSerialName() {
        return this.nombre.getSerialName();
    }
}
