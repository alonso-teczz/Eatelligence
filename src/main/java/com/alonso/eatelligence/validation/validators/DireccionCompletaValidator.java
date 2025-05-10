package com.alonso.eatelligence.validation.validators;

import com.alonso.eatelligence.model.dto.DireccionOpcionalDTO;
import com.alonso.eatelligence.validation.annotations.DireccionCompleta;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DireccionCompletaValidator implements ConstraintValidator<DireccionCompleta, DireccionOpcionalDTO> {

    /**
     * Valida que si se ha rellenado alguno de los campos de la dirección, se
     * hayan rellenado todos. Si no se ha rellenado ninguno, se considera válido.
     * 
     * @param direccion la dirección a validar
     * @param context   contexto de validación
     * @return true si es válido, false en caso contrario
     */
    @Override
    public boolean isValid(DireccionOpcionalDTO direccion, ConstraintValidatorContext context) {
        if (direccion == null) return true;

        boolean algunoRellenado = isFilled(direccion);

        if (!algunoRellenado) return true;

        boolean valido = true;

        context.disableDefaultConstraintViolation();

        //# Añade errores por campo si falta alguno
        
        if (isBlank(direccion.getCalle())) {
            context.buildConstraintViolationWithTemplate("La calle es obligatoria")
                   .addPropertyNode("calle").addConstraintViolation();
            valido = false;
        }

        if (isBlank(direccion.getNumCalle())) {
            context.buildConstraintViolationWithTemplate("El número es obligatorio")
                   .addPropertyNode("numCalle").addConstraintViolation();
            valido = false;
        }

        if (isBlank(direccion.getCiudad())) {
            context.buildConstraintViolationWithTemplate("La ciudad es obligatoria")
                   .addPropertyNode("ciudad").addConstraintViolation();
            valido = false;
        }

        if (isBlank(direccion.getProvincia())) {
            context.buildConstraintViolationWithTemplate("La provincia es obligatoria")
                   .addPropertyNode("provincia").addConstraintViolation();
            valido = false;
        }

        if (isBlank(direccion.getCodigoPostal())) {
            context.buildConstraintViolationWithTemplate("El código postal es obligatorio")
                   .addPropertyNode("codigoPostal").addConstraintViolation();
            valido = false;
        }

        return valido;
    }

    private boolean isFilled(DireccionOpcionalDTO d) {
        return !isBlank(d.getCalle()) ||
            !isBlank(d.getNumCalle()) ||
            !isBlank(d.getCiudad()) ||
            !isBlank(d.getProvincia()) ||
            !isBlank(d.getCodigoPostal());
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}