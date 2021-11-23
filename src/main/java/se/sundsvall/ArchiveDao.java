package se.sundsvall;

import se.sundsvall.vo.ArchiveBatchHistory;
import se.sundsvall.vo.ArchiveHistory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
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
    public void postArchiveBatchHistory(ArchiveBatchHistory archiveBatchHistory) {
        em.persist(archiveBatchHistory);
    }

    @Transactional
    public void updateArchiveBatchHistory(ArchiveBatchHistory archiveBatchHistory) {
        em.merge(archiveBatchHistory);
    }

    public ArchiveBatchHistory getArchiveBatchHistory(Long id) {
        ArchiveBatchHistory archiveBatchHistory = em.find(ArchiveBatchHistory.class, id);
        if (archiveBatchHistory == null) {
            throw new EntityNotFoundException("Can't find ArchiveBatchHistory for ID "
                    + id);
        }
        return archiveBatchHistory;
    }

    public List<ArchiveBatchHistory> getArchiveBatchHistory() {
        TypedQuery<ArchiveBatchHistory> archiveBatchHistoryList = em.createQuery("SELECT a FROM ArchiveBatchHistory a", ArchiveBatchHistory.class);

        return archiveBatchHistoryList.getResultList();
    }

    private String setWildcardIfNotPresent(String value) {
        if (value == null || value.isBlank() || value.isEmpty()) {
            value = "%";
        }
        return value;
    }
}
