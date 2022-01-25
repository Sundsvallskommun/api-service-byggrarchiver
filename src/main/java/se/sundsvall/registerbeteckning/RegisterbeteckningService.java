package se.sundsvall.registerbeteckning;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/v4")
@RegisterProvider(RegisterbeteckningOAuth2Filter.class)
@RegisterRestClient(configKey = "REGISTERBETECKNING")
@ApplicationScoped
public interface RegisterbeteckningService {

    @GET
    @Path("/referens/fritext/{beteckning}")
    @Produces("application/json")
    List<Registerbeteckningsreferens> getReferenser(@PathParam String beteckning,
            @QueryParam("statusFastighet") String status, @QueryParam("maxHits") int maxHits);

}
