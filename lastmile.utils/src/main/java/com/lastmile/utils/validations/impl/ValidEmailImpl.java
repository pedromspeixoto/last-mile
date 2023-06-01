package com.lastmile.utils.validations.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.lastmile.utils.validations.ValidEmail;

import org.apache.commons.validator.routines.EmailValidator;

public class ValidEmailImpl implements ConstraintValidator<ValidEmail, String> {

    @Override
        public void initialize(ValidEmail email) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext cxt) {
        if (null == email) {
            return true;
        }
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

}