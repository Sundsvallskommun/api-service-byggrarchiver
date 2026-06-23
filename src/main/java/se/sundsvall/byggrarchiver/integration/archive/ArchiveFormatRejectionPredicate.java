package se.sundsvall.byggrarchiver.integration.archive;

import java.util.List;
import java.util.function.Predicate;
import se.sundsvall.dept44.exception.ServerProblem;

/**
 * Decides whether a throwable from the Archive integration is a per-document content/format rejection rather than an
 * outage.
 * <p>
 * The Archive service answers HTTP 500 (mapped to {@link ServerProblem}) when it rejects a single document's
 * file format. That is a business condition, not unavailability, so it must not count toward the {@code archive}
 * circuit breaker / retry - otherwise a few bad documents in a batch open the breaker and block archiving of valid
 * ones. Wired in via {@code resilience4j.*.instances.archive.ignore-exception-predicate} (resilience4j instantiates
 * this class reflectively, hence the public no-arg constructor).
 * <p>
 * NOTE: workaround for the Archive service using 500 where it should use a 4xx (422) for content validation. Once the
 * Archive service returns 4xx, this predicate and the {@link #isFormatRejection(String)} matching in
 * {@code ArchiveAttachmentService} become dead code and should be removed.
 */
public final class ArchiveFormatRejectionPredicate implements Predicate<Throwable> {

	private static final List<String> FORMAT_REJECTION_MARKERS = List.of(
		"extension must be valid",
		"File format",
		"PreservationObjectConversionException");

	/**
	 * @param  message the exception message to inspect (typically {@code Problem.getMessage()})
	 * @return         true if the message indicates the Archive service rejected the document's format/extension
	 */
	public static boolean isFormatRejection(final String message) {
		return (message != null) && FORMAT_REJECTION_MARKERS.stream().anyMatch(message::contains);
	}

	@Override
	public boolean test(final Throwable throwable) {
		// Only a ServerProblem (5xx) is the misreported business 500. ClientProblem (4xx) is already ignored by the
		// breaker/retry ignore-exceptions list, and genuine outage 500s won't carry a format-rejection marker.
		return (throwable instanceof final ServerProblem serverProblem) && isFormatRejection(serverProblem.getMessage());
	}

}
