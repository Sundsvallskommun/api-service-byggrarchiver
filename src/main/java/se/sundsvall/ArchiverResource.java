package se.sundsvall;

import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.vo.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
public class ArchiverResource {

    @Inject
    Archiver archiver;

    @Inject
    ArchiveDao archiveDao;

    @GET
    @Path("archive-history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchiveHistory(@QueryParam("status") Status status) {
        List<ArchiveHistory> archiveHistoryList;
        if (status != null) {
            archiveHistoryList = archiveDao.getArchiveHistory(status);
        } else {
            archiveHistoryList = archiveDao.getArchiveHistory();
        }

        if (archiveHistoryList.isEmpty()) {
            throw new NotFoundException("ArchiveHistory not found");
        } else {
            return Response.ok(archiveHistoryList).build();
        }

    }

    @GET
    @Path("batch-history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBatchHistory() {
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistory();

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
    public Response postBatchJob(@NotNull @Valid BatchJob batchJob) throws ApplicationException {
        archiver.archiveByggrAttachments(batchJob.getStart(), batchJob.getEnd(), BatchTrigger.MANUAL);
        return Response.ok().build();
    }

    @PUT
    @Path("batch-job/{batchHistoryId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reRunBatchJob(@PathParam("batchHistoryId") Long batchHistoryId) throws ApplicationException {

        archiver.reRunBatch(batchHistoryId);
        return Response.ok().build();
    }
}
