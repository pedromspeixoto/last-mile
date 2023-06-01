package com.lastmile.utils.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.lastmile.utils.validations.impl.ValidIbanImpl;

@Documented
@Constraint(validatedBy = ValidIbanImpl.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIban {
    String message() default "invalid iban";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}