package se.sundsvall.validators;

import se.sundsvall.util.Constants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEnd {
    String message() default Constants.END_CAN_NOT_BE_BEFORE_START;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
