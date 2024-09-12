package se.sundsvall.byggrarchiver.api.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import se.sundsvall.byggrarchiver.api.validation.impl.StartBeforeEndValidator;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEnd {

	String message() default "End can not be before start";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
