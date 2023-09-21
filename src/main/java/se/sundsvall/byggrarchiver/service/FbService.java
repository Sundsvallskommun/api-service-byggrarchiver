package se.sundsvall.byggrarchiver.service;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import generated.sokigo.fb.FastighetDto;
import se.sundsvall.byggrarchiver.integration.fb.FbClient;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

@Service
public class FbService {

	@Value("${fb.username}")
	String fbUsername;
	@Value("${fb.password}")
	String fbPassword;
	@Value("${fb.database}")
	String fbDatabase;

	private final FbClient fbClient;

	public FbService(FbClient fbClient) {
		this.fbClient = fbClient;
	}

	public FastighetDto getPropertyInfoByFnr(Integer fnr) throws ApplicationException {
		List<FastighetDto> fastighetDtoList;
		try {
			fastighetDtoList = fbClient.getPropertyInfoByFnr(List.of(fnr), fbDatabase, fbUsername, fbPassword).getData();
		} catch (final AbstractThrowableProblem e) {
			throw Problem.valueOf(Status.SERVICE_UNAVAILABLE, "Request to fbService.getPropertyInfoByFnr(" + fnr + ") failed.");
		}

		if (isEmpty(fastighetDtoList)) {
			return null;
		}
		if (fastighetDtoList.size() > 1) {
			throw new ApplicationException("The response from fbService.getPropertyInfoByFnr([" + fnr + "]) contained more than one FastighetDto, that should not happen");
		} else {
			return fastighetDtoList.get(0);
		}
	}

}
