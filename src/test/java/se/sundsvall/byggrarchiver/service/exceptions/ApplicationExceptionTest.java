package se.sundsvall.byggrarchiver.service.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

@RunWith(SpringRunner.class)
class ApplicationExceptionTest {

	@Test
	void test1() {
		final var message = "test1";
		final var exception = assertThrows(ApplicationException.class, () -> {
			throw new ApplicationException(message);
		});

		assertEquals(message, exception.getMessage());
	}

	@Test
	void test2() {
		final var message = "test2";
		final var exception = assertThrows(ApplicationException.class, () -> {
			throw new ApplicationException(message, Problem.valueOf(Status.INTERNAL_SERVER_ERROR));
		});

		assertEquals(message, exception.getMessage());
		assertInstanceOf(ThrowableProblem.class, exception.getCause());
	}

}
