package se.sundsvall.sokigo.fb;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
@RegisterRestClient(configKey = "FB")
@ApplicationScoped
public interface FbService {
    @POST
    @Path("Fastighet/info/uuid")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ResponseDto getPropertyInfoByUuid(@RequestBody List<String> registerenheter, @QueryParam("Database") String database,
                                      @QueryParam("User") String user, @QueryParam("Password") String password);

    @POST
    @Path("Fastighet/info/fnr")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ResponseDto getPropertyInfoByFnr(@RequestBody List<Integer> fnrList, @QueryParam("Database") String database,
                                @QueryParam("User") String user, @QueryParam("Password") String password);

    @POST
    @Path("adress/search/fastighet/uuid")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ResponseDto getAddressInfoByUuid(@RequestBody List<String> registerenheter, @QueryParam("Database") String database,
                                     @QueryParam("User") String user, @QueryParam("Password") String password);
}
