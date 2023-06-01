package com.lastmile.utils.validations.impl;

import java.util.Arrays;
import java.util.Locale;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.lastmile.utils.validations.ValidCountryCode;

public class ValidCountryCodeImpl implements ConstraintValidator<ValidCountryCode, String> {

    @Override
        public void initialize(ValidCountryCode countryCode) {
    }

    @Override
    public boolean isValid(String countryCode, ConstraintValidatorContext cxt) {
        if (null == countryCode) {
            return true;
        }
        String[] countriesList = Locale.getISOCountries();
        return Arrays.asList(countriesList).contains(countryCode);
    }

}