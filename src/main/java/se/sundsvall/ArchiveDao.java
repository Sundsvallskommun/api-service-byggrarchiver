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
    public void postArchiveBatchHistory(BatchHistory batchHistory) {
        em.persist(batchHistory);
    }

    @Transactional
    public void updateArchiveBatchHistory(BatchHistory batchHistory) {
        em.merge(batchHistory);
    }

    public BatchHistory getArchiveBatchHistory(Long id) {
        BatchHistory batchHistory = em.find(BatchHistory.class, id);
        if (batchHistory == null) {
            throw new EntityNotFoundException("Can't find ArchiveBatchHistory for ID "
                    + id);
        }
        return batchHistory;
    }

    public List<BatchHistory> getArchiveBatchHistory() {
        TypedQuery<BatchHistory> archiveBatchHistoryList = em.createQuery("SELECT a FROM ArchiveBatchHistory a", BatchHistory.class);

        return archiveBatchHistoryList.getResultList();
    }

    private String setWildcardIfNotPresent(String value) {
        if (value == null || value.isBlank() || value.isEmpty()) {
            value = "%";
        }
        return value;
    }
}
