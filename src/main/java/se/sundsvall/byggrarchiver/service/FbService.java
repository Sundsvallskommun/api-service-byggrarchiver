package se.sundsvall.byggrarchiver.service;

import generated.sokigo.fb.FastighetDto;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.byggrarchiver.integration.fb.FbClient;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.service.exceptions.ExternalServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class FbService {

    @ConfigProperty(name = "fb.username")
    String fbUsername;
    @ConfigProperty(name = "fb.password")
    String fbPassword;
    @ConfigProperty(name = "fb.database")
    String fbDatabase;

    @Inject
    Logger log;

    @Inject
    @RestClient
    FbClient fbClient;

    public FastighetDto getPropertyInfoByFnr(Integer fnr) throws ApplicationException {
        List<FastighetDto> fastighetDtoList;
        try {
            fastighetDtoList = fbClient.getPropertyInfoByFnr(List.of(fnr), fbDatabase, fbUsername, fbPassword).getData();
        } catch (Exception e) {
            log.error("Request to fbService.getPropertyInfoByFnr(" + fnr + ") failed.", e);
            throw new ExternalServiceException();
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
