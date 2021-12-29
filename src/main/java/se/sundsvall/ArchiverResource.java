package se.sundsvall;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.validators.StartBeforeEnd;
import se.sundsvall.vo.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/")
public class ArchiverResource {

    @Inject
    Archiver archiver;

    @Inject
    ArchiveDao archiveDao;

    @GET
    @Path("archived/attachments")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = ArchiveHistory.class))),
            @APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = Information.class))),
            @APIResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = Information.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Information.class)))
    })
    public Response getArchiveHistory(@QueryParam("status") Status status, @QueryParam("batchHistoryId") Long batchHistoryId) {
        List<ArchiveHistory> archiveHistoryList = archiveDao.getArchiveHistories();

        if (batchHistoryId != null) {
            archiveHistoryList = archiveHistoryList.stream().filter(ah -> batchHistoryId.equals(ah.getBatchHistory().getId())).collect(Collectors.toList());
        }
        if (status != null) {
            archiveHistoryList = archiveHistoryList.stream().filter(ah -> status.equals(ah.getStatus())).collect(Collectors.toList());
        }

        if (archiveHistoryList.isEmpty()) {
            throw new NotFoundException("ArchiveHistory not found");
        } else {
            return Response.ok(archiveHistoryList).build();
        }

    }

    @GET
    @Path("batch-jobs")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = BatchHistory.class))),
            @APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = Information.class))),
            @APIResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = Information.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Information.class)))
    })
    public Response getBatchHistory() {
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistories();

        if (batchHistoryList.isEmpty()) {
            throw new NotFoundException("BatchHistory not found");
        } else {
            return Response.ok(batchHistoryList).build();
        }
    }

    @POST
    @Path("batch-jobs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BatchHistory.class))),
            @APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = Information.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Information.class)))
    })
    public Response postBatchJob(@StartBeforeEnd @NotNull(message = "Request body must not be null") @Valid BatchJob batchJob) throws ApplicationException, ServiceException {
        return Response.ok(archiver.archiveByggrAttachments(batchJob.getStart(), batchJob.getEnd(), BatchTrigger.MANUAL)).build();
    }

    @POST
    @Path("batch-jobs/{batchHistoryId}/rerun")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BatchHistory.class))),
            @APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = Information.class))),
            @APIResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = Information.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Information.class)))
            })
    public Response reRunBatchJob(@PathParam("batchHistoryId") Long batchHistoryId) throws ApplicationException, ServiceException {
        return Response.ok(archiver.reRunBatch(batchHistoryId)).build();
    }
}
