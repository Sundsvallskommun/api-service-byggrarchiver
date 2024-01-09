package se.sundsvall.byggrarchiver.integration.fb;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import generated.sokigo.fb.ResponseDtoIEnumerableFastighetDto;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(
    name = FbIntegrationConfiguration.INTEGRATION_NAME,
    url = "${integration.fb.url}",
    configuration = FbIntegrationConfiguration.class
)
interface FbClient {

    @Retry(name = FbIntegrationConfiguration.INTEGRATION_NAME)
    @PostMapping(path = "Fastighet/info/fnr", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseDtoIEnumerableFastighetDto getPropertyInfoByFnr(@RequestBody List<Integer> fnrList);
}
