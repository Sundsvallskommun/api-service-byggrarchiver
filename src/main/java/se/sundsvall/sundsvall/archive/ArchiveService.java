package se.sundsvall.sundsvall.archive;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.exceptions.mappers.ServerResponseExceptionMapper;
import se.sundsvall.sundsvall.SundsvallsKommunOauth2Filter;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@RegisterProvider(SundsvallsKommunOauth2Filter.class)
@RegisterProvider(ServerResponseExceptionMapper.class)
@RegisterRestClient(configKey = "ARCHIVE")
@ApplicationScoped
public interface ArchiveService {

    @POST
    @Path("archive/byggr")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ArchiveResponse postArchive(@NotNull @Valid ByggRArchiveRequest archiveMessage) throws ServiceException;

}
