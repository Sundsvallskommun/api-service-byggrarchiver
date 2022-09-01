package se.sundsvall.byggrarchiver.api.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.validation.impl.StartBeforeEndValidator;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StartBeforeEndTest {

    @Mock
    private ConstraintValidatorContext constraintValidatorContextMock;

    @InjectMocks
    private StartBeforeEndValidator validator;

    @Test
    void valid() {
        final BatchJob batchJob = BatchJob.builder()
                .start(LocalDate.now().minusDays(3))
                .end(LocalDate.now())
                .build();

        assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();

    }

    @Test
    void validSameDate() {
        final BatchJob batchJob = BatchJob.builder()
                .start(LocalDate.now())
                .end(LocalDate.now())
                .build();

        assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();

    }

    @Test
    void invalid() {
        final BatchJob batchJob = BatchJob.builder()
                .start(LocalDate.now())
                .end(LocalDate.now().minusDays(3))
                .build();

        assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isFalse();

    }

    @Test
    void nullValue() {
        final BatchJob batchJob = BatchJob.builder()
                .end(LocalDate.now().minusDays(3))
                .build();

        assertThat(validator.isValid(batchJob, constraintValidatorContextMock)).isTrue();

        final BatchJob batchJob2 = BatchJob.builder()
                .start(LocalDate.now().minusDays(3))
                .build();

        assertThat(validator.isValid(batchJob2, constraintValidatorContextMock)).isTrue();
    }
}
