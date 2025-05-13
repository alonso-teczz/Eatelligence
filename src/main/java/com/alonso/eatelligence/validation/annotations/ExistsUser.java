package com.alonso.eatelligence.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.alonso.eatelligence.validation.validators.ExistsUserValidator;

@Documented
@Constraint(validatedBy = ExistsUserValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsUser {
    String message() default "Usuario no encontrado";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
