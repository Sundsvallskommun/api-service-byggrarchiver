package se.sundsvall.byggrarchiver.integration.archive;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.byggrarchiver.integration.archive.ArchiveIntegration.INTEGRATION_NAME;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
	name = INTEGRATION_NAME,
	url = "${integration.archive.url}",
	configuration = ArchiveConfiguration.class)
@CircuitBreaker(name = INTEGRATION_NAME)
interface ArchiveClient {

	@PostMapping(path = "/{municipalityId}/archive/byggr", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ArchiveResponse postArchive(
		@PathVariable(name = "municipalityId") String municipalityId,
		@RequestBody ByggRArchiveRequest archiveMessage);

}
