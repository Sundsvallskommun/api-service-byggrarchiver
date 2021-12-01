package se.sundsvall.sundsvall.archive;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.sundsvall.sundsvall.SundsvallsKommunOauth2Filter;
import se.sundsvall.sundsvall.casemanagement.Attachment;
import se.sundsvall.exceptions.mappers.ServerResponseExceptionMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/1.0")
@RegisterProvider(SundsvallsKommunOauth2Filter.class)
@RegisterProvider(ServerResponseExceptionMapper.class)
@RegisterRestClient(configKey = "ARCHIVE")
@ApplicationScoped
public interface ArchiveService {

    @POST
    @Path("/documents")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ArchiveResponse postArchive(@NotNull @Valid Attachment attachment);

}
