package se.sundsvall.casemanagement;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/2.1")
@RegisterProvider(CaseManagementOauth2Filter.class)
@RegisterRestClient(configKey = "CASE-MANAGEMENT")
@ApplicationScoped
public interface CaseManagementService {

    @GET
    @Path("/cases/closed/documents/archive")
    @Produces(MediaType.APPLICATION_JSON)
    List<Attachment> getDocuments(@NotNull
                                  @Schema(description = "Startdatum på sökningen.", format = "date", example = "2022-01-01")
                                  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                                  @QueryParam("search-start") String searchStart,
                                  @Schema(description = "Slutdatum på sökningen. Default = dagens datum.", format = "date", example = "2022-01-01")
                                  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                                  @QueryParam("search-end") String searchEnd,
                                  @QueryParam("system-type") SystemType systemType);
}
