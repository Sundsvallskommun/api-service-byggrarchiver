package se.sundsvall.byggrarchiver.integration.fb;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.byggrarchiver.integration.fb.FbIntegrationConfiguration.INTEGRATION_NAME;

import generated.sokigo.fb.ResponseDtoIEnumerableFastighetDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
	name = INTEGRATION_NAME,
	url = "${integration.fb.url}",
	configuration = FbIntegrationConfiguration.class)
@CircuitBreaker(name = INTEGRATION_NAME)
public interface FbClient {

	@Retry(name = INTEGRATION_NAME)
	@PostMapping(path = "Fastighet/info/fnr", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ResponseDtoIEnumerableFastighetDto getPropertyInfoByFnr(@RequestBody List<Integer> fnrList);

}
