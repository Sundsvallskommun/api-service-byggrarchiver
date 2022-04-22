package se.sundsvall.byggrarchiver.api;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.model.BatchTrigger;
import se.sundsvall.byggrarchiver.api.model.Information;
import se.sundsvall.byggrarchiver.api.model.Status;
import se.sundsvall.byggrarchiver.api.validators.StartBeforeEnd;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/")
public class ByggrArchiverResource {

    @Inject
    ByggrArchiverService byggrArchiverService;

    @Inject
    ArchiveHistoryRepository archiveHistoryRepository;

    @Inject
    BatchHistoryRepository batchHistoryRepository;

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
        List<ArchiveHistory> archiveHistoryList = archiveHistoryRepository.getArchiveHistories();

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
        List<BatchHistory> batchHistoryList = batchHistoryRepository.getBatchHistories();

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
    public Response postBatchJob(@StartBeforeEnd @NotNull(message = "Request body must not be null") @Valid BatchJob batchJob) throws ApplicationException {
        return Response.ok(byggrArchiverService.runBatch(batchJob.getStart(), batchJob.getEnd(), BatchTrigger.MANUAL)).build();
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
    public Response reRunBatchJob(@PathParam("batchHistoryId") Long batchHistoryId) throws ApplicationException {
        return Response.ok(byggrArchiverService.reRunBatch(batchHistoryId)).build();
    }
}
