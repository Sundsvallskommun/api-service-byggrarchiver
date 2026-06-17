package se.sundsvall.byggrarchiver.api.validation;

import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.validation.impl.StartBeforeEndValidator;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StartBeforeEndTest {

	private static final LocalDate DATE = LocalDate.of(2024, Month.JANUARY, 16);

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@InjectMocks
	private StartBeforeEndValidator validator;

	@Test
	void valid() {
		var batchJob = BatchJob.builder()
			.withStart(DATE.minusDays(3))
			.withEnd(DATE)
			.build();

		assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();
	}

	@Test
	void validSameDate() {
		var batchJob = BatchJob.builder()
			.withStart(DATE)
			.withEnd(DATE)
			.build();

		assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();
	}

	@Test
	void invalid() {
		var batchJob = BatchJob.builder()
			.withStart(DATE)
			.withEnd(DATE.minusDays(3))
			.build();

		assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isFalse();
	}

	@Test
	void nullValue() {
		var batchJob = BatchJob.builder()
			.withEnd(DATE.minusDays(3))
			.build();

		assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();

		var batchJob2 = BatchJob.builder()
			.withStart(DATE.minusDays(3))
			.build();

		assertThat(validator.isValid(batchJob2, constraintValidatorContextMock)).isTrue();
	}

}
