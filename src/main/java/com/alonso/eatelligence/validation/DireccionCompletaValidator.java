package com.alonso.eatelligence.validation;

import com.alonso.eatelligence.model.dto.DireccionRegistroDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DireccionCompletaValidator implements ConstraintValidator<DireccionCompleta, DireccionRegistroDTO> {

    @Override
    public boolean isValid(DireccionRegistroDTO direccion, ConstraintValidatorContext context) {
        //! Si no hay nada rellenado, OK — es opcional
        if (direccion == null) {
            return true;
        }

        boolean existsEmptyField =
            notEmpty(direccion.getCalle()) ||
            notEmpty(direccion.getNumCalle()) ||
            notEmpty(direccion.getCiudad()) ||
            notEmpty(direccion.getProvincia()) ||
            notEmpty(direccion.getCodigoPostal());

        boolean allFilled =
            notEmpty(direccion.getCalle()) &&
            notEmpty(direccion.getNumCalle()) &&
            notEmpty(direccion.getCiudad()) &&
            notEmpty(direccion.getProvincia()) &&
            notEmpty(direccion.getCodigoPostal());

        //! Si no hay nada rellenado, OK — es opcional.
        //! Si hay alguno pero no todos, entonces es inválido.
        return !existsEmptyField || allFilled;
    }

    private boolean notEmpty(String s) {
        return s != null && !s.isBlank();
    }
}