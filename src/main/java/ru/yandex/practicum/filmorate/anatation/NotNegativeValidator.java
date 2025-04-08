package ru.yandex.practicum.filmorate.anatation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.exeption.NotNegativeIdException;

public class NotNegativeValidator implements ConstraintValidator<NotNegative, Long> {


    @Override
    public void initialize(NotNegative constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long aLong, ConstraintValidatorContext constraintValidatorContext) {
        if (aLong == null) {
            return true;
        }
        if (aLong < 0L) {
            throw new NotNegativeIdException("id не может быть меньше 0");
        }
        return true;
    }
}