package se.sundsvall.byggrarchiver.integration.archive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.dept44.exception.ClientProblem;
import se.sundsvall.dept44.exception.ServerProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class ArchiveFormatRejectionPredicateTest {

	private final ArchiveFormatRejectionPredicate predicate = new ArchiveFormatRejectionPredicate();

	@ParameterizedTest
	@ValueSource(strings = {
		"The file extension must be valid",
		"File format validation failed.",
		"... PreservationObjectConversionException ...",
		"[500 ...] [FormpipeProxyClient#postImport]: [{\"Message\":\"File format validation failed.\"}]"
	})
	void isFormatRejectionMatchesMarkers(final String message) {
		assertThat(ArchiveFormatRejectionPredicate.isFormatRejection(message)).isTrue();
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {
		"Internal Server Error",
		"Connection reset",
		"Some other archive failure"
	})
	void isFormatRejectionDoesNotMatchOtherMessages(final String message) {
		assertThat(ArchiveFormatRejectionPredicate.isFormatRejection(message)).isFalse();
	}

	@Test
	void testIgnoresFormatRejectionServerProblem() {
		final var problem = new ServerProblem(INTERNAL_SERVER_ERROR, "File format validation failed.");

		assertThat(predicate.test(problem)).isTrue();
	}

	@Test
	void testCountsGenericServerProblem() {
		final var problem = new ServerProblem(INTERNAL_SERVER_ERROR, "Archive is down");

		assertThat(predicate.test(problem)).isFalse();
	}

	@Test
	void testLeavesClientProblemToTheIgnoreExceptionsList() {
		// A ClientProblem (4xx) is handled by ignore-exceptions, not this predicate - even when it carries marker text.
		final var problem = new ClientProblem(BAD_REQUEST, "File format is not allowed");

		assertThat(predicate.test(problem)).isFalse();
	}

	@Test
	void testCountsUnrelatedThrowable() {
		assertThat(predicate.test(new RuntimeException("File format"))).isFalse();
	}

}
