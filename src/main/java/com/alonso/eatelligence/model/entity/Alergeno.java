package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alergenos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alergeno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NombreAlergeno nombre;

    @Column(nullable = false)
    @Builder.Default
    private Boolean oficial = true;

    public enum NombreAlergeno {
        GLUTEN("Gluten"),
        CRUSTACEOS("Crustáceos"),
        HUEVOS("Huevos"),
        PESCADO("Pescado"),
        CACAHUETES("Cacahuetes"),
        SOJA("Soja"),
        LACTEOS("Lácteos"),
        FRUTOS_SECOS("Frutos secos"),
        APIO("Apio"),
        MOSTAZA("Mostaza"),
        SESAMO("Sésamo"),
        SULFITOS("Sulfitos"),
        ALTRAMUCES("Altramuces"),
        MOLUSCOS("Moluscos");
    
        private final String serialName;
    
        NombreAlergeno(String serialName) {
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