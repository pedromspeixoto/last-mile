package com.lastmile.utils.validations.impl;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.lastmile.utils.validations.ValidBirthDate;

public class ValidBirthDateImpl implements ConstraintValidator<ValidBirthDate, Date> {

    @Override
    public void initialize(ValidBirthDate date) {
    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext cxt) {
        if ( null == date) {
            return true;
        }
        try {
            Calendar thirtyDaysAgo = Calendar.getInstance();
            thirtyDaysAgo.add(Calendar.DAY_OF_MONTH, -30);
            Date thirtyDaysAgoDate = thirtyDaysAgo.getTime();
            if (date.before(thirtyDaysAgoDate)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;

    }

}