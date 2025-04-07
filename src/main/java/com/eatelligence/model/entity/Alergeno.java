package com.eatelligence.model.entity;

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

    @Column(nullable = false, unique = true)
    private String nombre;
}