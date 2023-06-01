package com.lastmile.utils.validations.impl;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.lastmile.utils.validations.Validator;
import com.lastmile.utils.validations.ValidScheduledDate;

public class ValidScheduleDateImpl implements ConstraintValidator<ValidScheduledDate, Date> {

    private Integer LAST_MILE_FUTURE_DATE_OFFSET = 30;

    @Override
    public void initialize(ValidScheduledDate date) {
    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext cxt) {
        if ( null == date) {
            return true;
        }
        try {
            if (Validator.isDateInTheFuture(date, Long.valueOf(LAST_MILE_FUTURE_DATE_OFFSET))) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;

    }

}