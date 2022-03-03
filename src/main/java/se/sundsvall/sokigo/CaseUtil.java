package se.sundsvall.sokigo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.sokigo.fb.FbService;
import se.sundsvall.sokigo.fb.vo.FastighetDto;

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
    @RestClient
    FbService fbService;

    public FastighetDto getPropertyInfoByFnr(Integer fnr) throws ApplicationException {
        List<FastighetDto> fastighetDtoList = fbService.getPropertyInfoByFnr(List.of(fnr), fbDatabase, fbUsername, fbPassword).getData();

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
