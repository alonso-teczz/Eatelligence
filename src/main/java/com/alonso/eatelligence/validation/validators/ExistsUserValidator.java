package com.alonso.eatelligence.validation.validators;

import com.alonso.eatelligence.service.imp.UsuarioServiceImp;
import com.alonso.eatelligence.validation.annotations.ExistsUser;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ExistsUserValidator implements ConstraintValidator<ExistsUser, String> {

    @Autowired
    private UsuarioServiceImp usuarioService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && this.usuarioService.existsByUsername(username);
    }
}