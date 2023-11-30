package se.sundsvall.byggrarchiver.service;

import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.bygglov.FastighetTyp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.util.List;


@Service
public class FastighetService {
	@Autowired
	private FbIntegration fbIntegration;

	FastighetTyp getFastighet(final List<ArendeFastighet> arendeFastighetList) throws ApplicationException {
		var fastighet = new FastighetTyp();

		for (var arendeFastighet : arendeFastighetList) {
			if (arendeFastighet != null && arendeFastighet.isArHuvudObjekt()) {
				var fastighetDto = fbIntegration.getPropertyInfoByFnr(arendeFastighet.getFastighet().getFnr());

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
