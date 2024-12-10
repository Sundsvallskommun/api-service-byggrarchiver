package se.sundsvall.byggrarchiver.api.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.validation.StartBeforeEnd;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BatchJob> {

	@Override
	public boolean isValid(final BatchJob batchJob, final ConstraintValidatorContext constraintValidatorContext) {
		if (batchJob.getStart() == null || batchJob.getEnd() == null) {
			return true;
		}

		return batchJob.getStart().isEqual(batchJob.getEnd()) || batchJob.getStart().isBefore(batchJob.getEnd());
	}

}
