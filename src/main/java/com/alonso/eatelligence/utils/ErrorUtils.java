package com.alonso.eatelligence.utils;

import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ErrorUtils {

    /**
     * Conserva solo el primer error encontrado según el orden de campos indicado,
     * y lo vuelve a añadir al modelo con su BindingResult.
     *
     * @param originalResult El BindingResult original con todos los errores.
     * @param dto            El objeto de formulario (ej. ClienteRegistroDTO).
     * @param model          El modelo de Spring MVC.
     * @param camposOrdenados Lista de nombres de campos en orden deseado.
     * @param nombreAtributo El nombre del atributo de modelo (ej. "registroUsuario").
     */
    public static void filtrarPrimerError(
        BindingResult originalResult,
        Object dto,
        Model model,
        String nombreAtributo,
        List<String> camposOrdenados
    ) {
        BeanPropertyBindingResult nuevoResult = new BeanPropertyBindingResult(dto, nombreAtributo);

        for (String campo : camposOrdenados) {
            if (originalResult.hasFieldErrors(campo)) {
                FieldError err = originalResult.getFieldError(campo);
                nuevoResult.rejectValue(campo, err.getCode(), err.getDefaultMessage());
                break;
            }
        }

        model.addAttribute(nombreAtributo, dto);
        model.addAttribute("org.springframework.validation.BindingResult." + nombreAtributo, nuevoResult);
    }
}