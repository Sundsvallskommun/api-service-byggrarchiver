package se.sundsvall.byggrarchiver.integration.fb;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import generated.sokigo.fb.FastighetDto;

@Component
@EnableConfigurationProperties(FbIntegrationProperties.class)
public class FbIntegration {

	private final FbIntegrationProperties properties;

	private final FbClient fbClient;

	public FbIntegration(final FbIntegrationProperties properties, final FbClient fbClient) {
		this.properties = properties;
		this.fbClient = fbClient;
	}

	public FastighetDto getPropertyInfoByFnr(final Integer fnr) throws ApplicationException {
		List<FastighetDto> fastighetDtoList;
		try {
			fastighetDtoList = fbClient.getPropertyInfoByFnr(List.of(fnr)).getData();
		} catch (AbstractThrowableProblem e) {
			throw Problem.valueOf(Status.SERVICE_UNAVAILABLE, "Request to fbService.getPropertyInfoByFnr(" + fnr + ") failed.");
		}

		if (fastighetDtoList.isEmpty()) {
			return null;
		} else if (fastighetDtoList.size() > 1) {
			throw new ApplicationException("The response from fbService.getPropertyInfoByFnr([" + fnr + "]) contained more than one FastighetDto, that should not happen");
		} else {
			return fastighetDtoList.get(0);
		}
	}

}
