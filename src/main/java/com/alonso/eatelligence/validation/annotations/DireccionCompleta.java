package com.alonso.eatelligence.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.alonso.eatelligence.validation.validators.DireccionCompletaValidator;

@Documented
@Constraint(validatedBy = DireccionCompletaValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DireccionCompleta {
    String message() default "Si se introduce una dirección personal, todos los campos deben estar completos";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
