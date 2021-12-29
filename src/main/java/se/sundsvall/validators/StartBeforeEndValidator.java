package se.sundsvall.validators;

import se.sundsvall.vo.BatchJob;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BatchJob> {
    @Override
    public boolean isValid(BatchJob batchJob, ConstraintValidatorContext constraintValidatorContext) {
        if (batchJob.getStart() == null || batchJob.getEnd() == null) {
            return true;
        }

        return !batchJob.getStart().isAfter(batchJob.getEnd());
    }
}
