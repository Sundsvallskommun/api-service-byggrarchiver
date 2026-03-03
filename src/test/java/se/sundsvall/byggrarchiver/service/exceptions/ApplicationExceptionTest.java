package se.sundsvall.byggrarchiver.service.exceptions;

import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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
			throw new ApplicationException(message, Problem.valueOf(INTERNAL_SERVER_ERROR));
		});

		assertThat(exception.getMessage()).isEqualTo(message);
		assertThat(exception.getCause()).isInstanceOf(ThrowableProblem.class);
	}

}
