package se.sundsvall.byggrarchiver.api.validation.impl;

import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.validation.StartBeforeEnd;

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
