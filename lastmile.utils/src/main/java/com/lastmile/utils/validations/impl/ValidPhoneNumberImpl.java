package com.lastmile.utils.validations.impl;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.lastmile.utils.validations.ValidPhoneNumber;

public class ValidPhoneNumberImpl implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
        public void initialize(ValidPhoneNumber phoneNumber) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext cxt) {
        if ( null == phoneNumber) {
            return true;
        }
        String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
        Pattern pattern = Pattern.compile(allCountryRegex);

        return pattern.matcher(phoneNumber).matches();
    }

}