package com.alonso.eatelligence.model.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Column(nullable = false)
    private String calle;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String provincia;

    @Column(nullable = false)
    private String codigoPostal;

    @Column(nullable = false)
    private String numCalle;

    @Column
    private Double latitud;

    @Column
    private Double longitud;
}
