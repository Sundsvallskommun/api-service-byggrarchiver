package se.sundsvall.byggrarchiver.service.exceptions;

import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationExceptionTest {

	@Test
	void test1() {
		final var message = "test1";
		final var exception = assertThrows(ApplicationException.class, () -> {
			throw new ApplicationException(message);
		});

		assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	void test2() {
		final var message = "test2";
		final var exception = assertThrows(ApplicationException.class, () -> {
			throw new ApplicationException(message, Problem.valueOf(Status.INTERNAL_SERVER_ERROR));
		});

		assertThat(exception.getMessage()).isEqualTo(message);
		assertThat(exception.getCause()).isInstanceOf(ThrowableProblem.class);
	}

}
