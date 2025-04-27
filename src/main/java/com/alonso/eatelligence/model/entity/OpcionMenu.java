package com.alonso.eatelligence.model.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "opciones_menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpcionMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String url;

    @Column(nullable = false, length = 50)
    private String seccion;

    @Column
    private Integer orden;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean activo = true;

    @ManyToMany(mappedBy = "opciones")
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private Set<Rol> roles = new HashSet<>();

}