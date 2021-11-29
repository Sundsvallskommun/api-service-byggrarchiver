package se.sundsvall;

import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.ArchiveHistory;

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

    @Transactional
    public void postArchiveHistory(ArchiveHistory archiveHistory){
        em.persist(archiveHistory);
    }

    public List<ArchiveHistory> getArchiveHistory(String documentId) {
        TypedQuery<ArchiveHistory> archiveHistoryList = em.createQuery("SELECT a FROM ArchiveHistory a WHERE a.documentId LIKE :documentId", ArchiveHistory.class)
                .setParameter("documentId", documentId);

        return archiveHistoryList.getResultList();
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
            throw new EntityNotFoundException("Can't find BatchHistory for ID "
                    + id);
        }
        return batchHistory;
    }

    public List<BatchHistory> getBatchHistory() {
        TypedQuery<BatchHistory> batchHistoryList = em.createQuery("SELECT a FROM BatchHistory a", BatchHistory.class);

        return batchHistoryList.getResultList();
    }

    private String setWildcardIfNotPresent(String value) {
        if (value == null || value.isBlank() || value.isEmpty()) {
            value = "%";
        }
        return value;
    }
}
