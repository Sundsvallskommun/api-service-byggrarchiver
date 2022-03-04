package se.sundsvall.sokigo;

import generated.sokigo.fb.FastighetDto;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ExternalServiceException;
import se.sundsvall.sokigo.fb.FbService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class CaseUtil {

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
    FbService fbService;

    public FastighetDto getPropertyInfoByFnr(Integer fnr) throws ApplicationException {
        List<FastighetDto> fastighetDtoList;
        try {
            fastighetDtoList = fbService.getPropertyInfoByFnr(List.of(fnr), fbDatabase, fbUsername, fbPassword).getData();
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

    public String byteArrayToBase64(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(byteArray);
    }

}
