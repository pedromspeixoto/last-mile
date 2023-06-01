package com.lastmile.utils.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.lastmile.utils.validations.impl.ValidRatingImpl;

@Documented
@Constraint(validatedBy = ValidRatingImpl.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRating {
    String message() default "invalid rating";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}