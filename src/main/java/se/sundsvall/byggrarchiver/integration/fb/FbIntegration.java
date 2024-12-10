package se.sundsvall.byggrarchiver.integration.fb;

import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.bygglov.FastighetTyp;
import generated.sokigo.fb.FastighetDto;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

@Component
@EnableConfigurationProperties(FbIntegrationProperties.class)
public class FbIntegration {

	private final FbClient fbClient;

	public FbIntegration(final FbClient fbClient) {
		this.fbClient = fbClient;
	}

	public FastighetTyp getFastighet(final List<ArendeFastighet> arendeFastighetList) throws ApplicationException {
		final var fastighet = new FastighetTyp();

		for (final var arendeFastighet : arendeFastighetList) {
			if ((arendeFastighet != null) && arendeFastighet.isArHuvudObjekt()) {
				final var fastighetDto = getPropertyInfoByFnr(arendeFastighet.getFastighet().getFnr());

				if (fastighetDto != null) {
					fastighet.setFastighetsbeteckning(fastighetDto.getKommun() + " " + fastighetDto.getBeteckning());
					fastighet.setTrakt(fastighetDto.getTrakt());
					fastighet.setObjektidentitet(fastighetDto.getUuid().toString());
				}
			}
		}

		return fastighet;
	}

	public FastighetDto getPropertyInfoByFnr(final Integer fnr) throws ApplicationException {
		final List<FastighetDto> fastighetDtoList;
		try {
			fastighetDtoList = fbClient.getPropertyInfoByFnr(List.of(fnr)).getData();
		} catch (final AbstractThrowableProblem e) {
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
