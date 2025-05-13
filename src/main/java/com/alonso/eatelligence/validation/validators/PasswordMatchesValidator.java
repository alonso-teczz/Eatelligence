package com.alonso.eatelligence.validation.validators;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.validation.annotations.PasswordMatches;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto instanceof ClienteRegistroDTO cliente) {
            return cliente.getPassword() != null && cliente.getPassword().equals(cliente.getRepeatPass());
        }

        if (dto instanceof RestauranteRegistroDTO restaurante) {
            if (restaurante.getPropietario() == null) return false;
            return restaurante.getPropietario().getPassword() != null &&
                restaurante.getPropietario().getPassword().equals(restaurante.getPropietario().getRepeatPass());
        }

        return false;
    }
}