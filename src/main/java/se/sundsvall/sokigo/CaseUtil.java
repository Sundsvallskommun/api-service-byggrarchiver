package se.sundsvall.sokigo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.registerbeteckning.RegisterbeteckningService;
import se.sundsvall.registerbeteckning.Registerbeteckningsreferens;
import se.sundsvall.sokigo.fb.FastighetDto;
import se.sundsvall.sokigo.fb.FbPropertyInfo;
import se.sundsvall.sokigo.fb.FbService;
import se.sundsvall.sokigo.fb.ResponseDto;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.Information;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class CaseUtil {

    @ConfigProperty(name = "fb.username")
    String fbUsername;
    @ConfigProperty(name = "fb.password")
    String fbPassword;

    @Inject
    Logger log;

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Inject
    @RestClient
    RegisterbeteckningService registerbeteckningService;

    @Inject
    @RestClient
    FbService fbService;

    public FbPropertyInfo getPropertyInfoByPropertyDesignation(String propertyDesignation) {
        propertyDesignation = propertyDesignation.trim().toUpperCase();

        FbPropertyInfo propertyInfo = new FbPropertyInfo();

        List<Registerbeteckningsreferens> registerbeteckningsreferenser = registerbeteckningService
                .getReferenser(propertyDesignation, Constants.LANTMATERIET_REFERENS_STATUS_GALLANDE, 1);

        // Set FNR if the propertyDesignation in the response matches the request
        if (registerbeteckningsreferenser != null && !registerbeteckningsreferenser.isEmpty()
                && registerbeteckningsreferenser.get(0).getBeteckning().equalsIgnoreCase(propertyDesignation)) {

            ResponseDto fnrResponse = fbService.getPropertyInfoByUuid(
                    List.of(registerbeteckningsreferenser.get(0).getRegisterenhet()), Constants.FB_DATABASE,
                    fbUsername, fbPassword);

            if (fnrResponse != null && fnrResponse.getData() != null && !fnrResponse.getData().isEmpty()
                    && fnrResponse.getData().get(0).getFnr() != null) {

                propertyInfo.setFnr(fnrResponse.getData().get(0).getFnr());

                ResponseDto addressResponse = fbService.getAddressInfoByUuid(
                        List.of(registerbeteckningsreferenser.get(0).getRegisterenhet()), Constants.FB_DATABASE,
                        fbUsername, fbPassword);

                if (addressResponse != null && addressResponse.getData() != null && !addressResponse.getData().isEmpty()
                        && addressResponse.getData().get(0).getGrupp() != null
                        && !addressResponse.getData().get(0).getGrupp().isEmpty()
                        && addressResponse.getData().get(0).getGrupp().get(0).getAdressplatsId() != null) {
                    propertyInfo.setAddressId(addressResponse.getData().get(0).getGrupp().get(0).getAdressplatsId());
                }

                if (propertyInfo.getFnr() != null) {
                    return propertyInfo;
                }

            }
        }

        // If we reach this code, we did not find the right property
        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                .entity(new Information(Constants.RFC_LINK_BAD_REQUEST, Response.Status.BAD_REQUEST.getReasonPhrase(),
                        Response.Status.BAD_REQUEST.getStatusCode(),
                        ERR_MSG_PROPERTY_DESIGNATION_NOT_FOUND(propertyDesignation),
                        "facility.address.propertyDesignation"))
                .build());

    }

    public static String ERR_MSG_PROPERTY_DESIGNATION_NOT_FOUND(String propertyDesignation) {
        return "The specified propertyDesignation(" + propertyDesignation + ") could not be found";
    }

    public FastighetDto getPropertyInfoByFnr(Integer fnr) throws ApplicationException {
        List<FastighetDto> fastighetDtoList = fbService.getPropertyInfoByFnr(List.of(fnr), Constants.FB_DATABASE, fbUsername, fbPassword).getData();

        if (fastighetDtoList.isEmpty()) {
            return null;
        } else if (fastighetDtoList.size() > 1) {
            throw new ApplicationException("The response from fbService.getPropertyInfoByFnr([" + fnr + "]) contained more than one FastighetDto, that should not happen");
        } else {
            return fastighetDtoList.get(0);
        }
    }

    public byte[] base64ToByteArray(String base64) {

        byte[] decoded;

        if (base64.startsWith("data:")) {
            base64 = base64.substring(base64.indexOf(",") + 1);
        }

        decoded = Base64.getDecoder().decode(base64.getBytes());

        return decoded;
    }

    public String byteArrayToBase64(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(byteArray);
    }

}
