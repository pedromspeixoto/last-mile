package com.lastmile.utils.validations.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.lastmile.utils.validations.ValidUrl;

import org.apache.commons.validator.routines.UrlValidator;

public class ValidUrlImpl implements ConstraintValidator<ValidUrl, String> {

    @Override
        public void initialize(ValidUrl url) {
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext cxt) {
        if (null == url) {
            return true;
        }
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(url)) {
           return true;
        }
        return false;
    }

}