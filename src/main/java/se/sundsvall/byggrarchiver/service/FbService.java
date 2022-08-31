package se.sundsvall.byggrarchiver.service;

import generated.sokigo.fb.FastighetDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.byggrarchiver.integration.fb.FbClient;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.util.List;

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
