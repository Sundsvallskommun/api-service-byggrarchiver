package se.sundsvall.byggrarchiver.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.bygglov.FastighetTyp;

@Service
public class FastighetService {

	private final FbIntegration fbIntegration;

	public FastighetService(final FbIntegration fbIntegration) {
		this.fbIntegration = fbIntegration;
	}

	FastighetTyp getFastighet(final List<ArendeFastighet> arendeFastighetList) throws ApplicationException {
		final var fastighet = new FastighetTyp();

		for (final var arendeFastighet : arendeFastighetList) {
			if ((arendeFastighet != null) && arendeFastighet.isArHuvudObjekt()) {
				final var fastighetDto = fbIntegration.getPropertyInfoByFnr(arendeFastighet.getFastighet().getFnr());

				if (fastighetDto != null) {
					fastighet.setFastighetsbeteckning(fastighetDto.getKommun() + " " + fastighetDto.getBeteckning());
					fastighet.setTrakt(fastighetDto.getTrakt());
					fastighet.setObjektidentitet(fastighetDto.getUuid().toString());
				}
			}
		}

		return fastighet;
	}

}
