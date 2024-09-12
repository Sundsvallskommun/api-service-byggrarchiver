package se.sundsvall.byggrarchiver.api.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.validation.impl.StartBeforeEndValidator;

@ExtendWith(MockitoExtension.class)
class StartBeforeEndTest {

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@InjectMocks
	private StartBeforeEndValidator validator;

	@Test
	void valid() {
		var batchJob = BatchJob.builder()
			.withStart(LocalDate.now().minusDays(3))
			.withEnd(LocalDate.now())
			.build();

		assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();
	}

	@Test
	void validSameDate() {
		var batchJob = BatchJob.builder()
			.withStart(LocalDate.now())
			.withEnd(LocalDate.now())
			.build();

		assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();
	}

	@Test
	void invalid() {
		var batchJob = BatchJob.builder()
			.withStart(LocalDate.now())
			.withEnd(LocalDate.now().minusDays(3))
			.build();

		assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isFalse();
	}

	@Test
	void nullValue() {
		var batchJob = BatchJob.builder()
			.withEnd(LocalDate.now().minusDays(3))
			.build();

		assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();

		var batchJob2 = BatchJob.builder()
			.withStart(LocalDate.now().minusDays(3))
			.build();

		assertThat(validator.isValid(batchJob2, constraintValidatorContextMock)).isTrue();
	}

}
