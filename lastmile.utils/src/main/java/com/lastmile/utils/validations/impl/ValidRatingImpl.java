package com.lastmile.utils.validations.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.lastmile.utils.validations.ValidRating;

public class ValidRatingImpl implements ConstraintValidator<ValidRating, Integer> {

    @Override
    public void initialize(ValidRating rating) {
    }

    @Override
    public boolean isValid(Integer rating, ConstraintValidatorContext cxt) {
        if ( null == rating) {
            return true;
        }
        if (rating > 0 && rating <= 5) {
            return true;
        }
        return false;
    }
}