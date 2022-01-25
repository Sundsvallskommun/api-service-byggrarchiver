package se.sundsvall;

import se.sundsvall.vo.SystemType;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ArchiveDao {

    @Inject
    EntityManager em;

    public List<ArchiveHistory> getArchiveHistories() {
        TypedQuery<ArchiveHistory> archiveHistoryList = em.createQuery("SELECT a FROM ArchiveHistory a", ArchiveHistory.class);
        return archiveHistoryList.getResultList();
    }

    public List<ArchiveHistory> getArchiveHistories(Long batchHistoryId) {
        return em.createQuery("SELECT a FROM ArchiveHistory a WHERE a.batchHistory LIKE :batchHistory", ArchiveHistory.class)
                .setParameter("batchHistory", getBatchHistory(batchHistoryId)).getResultList();
    }

    public List<ArchiveHistory> getArchiveHistories(Status status) {
        TypedQuery<ArchiveHistory> archiveHistoryList = em.createQuery("SELECT a FROM ArchiveHistory a WHERE a.status LIKE :status", ArchiveHistory.class)
                .setParameter("status", status);
        return archiveHistoryList.getResultList();
    }

    public ArchiveHistory getArchiveHistory(String documentId, SystemType systemType) throws ApplicationException {
        TypedQuery<ArchiveHistory> archiveHistoryList = em.createQuery("SELECT a FROM ArchiveHistory a WHERE a.documentId LIKE :documentId AND a.systemType LIKE :systemType", ArchiveHistory.class)
                .setParameter("documentId", documentId)
                .setParameter("systemType", systemType);

        if (archiveHistoryList.getResultList().size() > 1) {
            throw new ApplicationException("It should not be more than one row in db for documentId: " + documentId + " and systemType: " + systemType);
        } else if (archiveHistoryList.getResultList().size() == 1) {
            return archiveHistoryList.getResultList().get(0);
        } else {
            return null;
        }
    }

    public List<BatchHistory> getBatchHistories() {
        TypedQuery<BatchHistory> batchHistoryList = em.createQuery("SELECT a FROM BatchHistory a", BatchHistory.class);

        return batchHistoryList.getResultList();
    }


    @Transactional
    public void postArchiveHistory(ArchiveHistory archiveHistory) {
        em.persist(archiveHistory);
    }

    @Transactional
    public void updateArchiveHistory(ArchiveHistory archiveHistory) {
        em.merge(archiveHistory);
    }

    @Transactional
    public void postBatchHistory(BatchHistory batchHistory) {
        em.persist(batchHistory);
    }

    @Transactional
    public void updateBatchHistory(BatchHistory batchHistory) {
        em.merge(batchHistory);
    }

    public BatchHistory getBatchHistory(Long id) {
        BatchHistory batchHistory = em.find(BatchHistory.class, id);
        if (batchHistory == null) {
            throw new EntityNotFoundException("Can't find BatchHistory with ID: " + id);
        }
        return batchHistory;
    }


}
