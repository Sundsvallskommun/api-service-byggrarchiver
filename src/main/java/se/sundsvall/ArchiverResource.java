package se.sundsvall;

import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
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
    @Path("batches")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBatchHistory() {
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistories();

        if (batchHistoryList.isEmpty()) {
            throw new NotFoundException("BatchHistory not found");
        } else {
            return Response.ok(batchHistoryList).build();
        }
    }

    @POST
    @Path("batch-job")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postBatchJob(@NotNull(message = "Request body must not be null") @Valid BatchJob batchJob) throws ApplicationException, ServiceException {
        return Response.ok(archiver.archiveByggrAttachments(batchJob.getStart(), batchJob.getEnd(), BatchTrigger.MANUAL)).build();
    }

    @PUT
    @Path("batch-job/{batchHistoryId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reRunBatchJob(@PathParam("batchHistoryId") Long batchHistoryId) throws ApplicationException, ServiceException {

        archiver.reRunBatch(batchHistoryId);
        return Response.ok().build();
    }
}
