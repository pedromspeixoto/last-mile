package com.lastmile.utils.validations.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.lastmile.utils.validations.ValidIban;

import org.apache.commons.validator.routines.IBANValidator;

public class ValidIbanImpl implements ConstraintValidator<ValidIban, String> {

    @Override
        public void initialize(ValidIban iban) {
    }

    @Override
    public boolean isValid(String iban, ConstraintValidatorContext cxt) {
        if (null == iban) {
            return true;
        }
        IBANValidator validator = IBANValidator.getInstance();
        return validator.isValid(iban);
    }

}