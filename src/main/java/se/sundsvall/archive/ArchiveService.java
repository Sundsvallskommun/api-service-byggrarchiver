package se.sundsvall.archive;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.sundsvall.casemanagement.CaseManagementOauth2Filter;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/1.0")
@RegisterProvider(CaseManagementOauth2Filter.class)
@RegisterRestClient(configKey = "ARCHIVE")
@ApplicationScoped
public interface ArchiveService {

    @GET
    @Path("/settings")
    @Produces(MediaType.APPLICATION_JSON)
    String postArchive();

}
