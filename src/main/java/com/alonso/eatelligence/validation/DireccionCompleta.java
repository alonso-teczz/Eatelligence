package com.alonso.eatelligence.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DireccionCompletaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DireccionCompleta {
    String message() default "Si se introduce una dirección personal, todos los campos deben estar completos";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
