package com.alonso.eatelligence.model.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un tramo de apertura/cierre semanal para el restaurante.
 * El formato JSON esperado / devuelto es, por ejemplo:
 * {
 *   "dia": "MONDAY",
 *   "apertura": "13:00",
 *   "cierre": "16:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDTO {

    @NotNull(message = "El día es obligatorio")
    private DayOfWeek dia;

    @NotNull(message = "La hora de apertura es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime apertura;

    @NotNull(message = "La hora de cierre es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime cierre;
}
