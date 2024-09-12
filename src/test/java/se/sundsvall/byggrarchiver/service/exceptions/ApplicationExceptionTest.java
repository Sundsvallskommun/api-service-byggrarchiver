package se.sundsvall.byggrarchiver.service.exceptions;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		String message = "test1";
		var exception = assertThrows(ApplicationException.class, () -> {
			throw new ApplicationException(message);
		});

		assertEquals(message, exception.getMessage());
	}

	@Test
	void test2() {
		String message = "test2";
		var exception = assertThrows(ApplicationException.class, () -> {
			throw new ApplicationException(message, Problem.valueOf(Status.INTERNAL_SERVER_ERROR));
		});

		assertEquals(message, exception.getMessage());
		assertTrue(exception.getCause() instanceof ThrowableProblem);
	}

}
