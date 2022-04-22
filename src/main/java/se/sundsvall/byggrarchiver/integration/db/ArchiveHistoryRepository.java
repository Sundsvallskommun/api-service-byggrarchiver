package se.sundsvall.byggrarchiver.integration.db;

import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class ArchiveHistoryRepository {

    @Inject
    EntityManager em;

    @Inject
    BatchHistoryRepository batchHistoryRepository;

    public List<ArchiveHistory> getArchiveHistories() {
        TypedQuery<ArchiveHistory> archiveHistoryList = em.createQuery("SELECT a FROM ArchiveHistory a", ArchiveHistory.class);
        return archiveHistoryList.getResultList();
    }

    public List<ArchiveHistory> getArchiveHistories(Long batchHistoryId) {
        return em.createQuery("SELECT a FROM ArchiveHistory a WHERE a.batchHistory LIKE :batchHistory", ArchiveHistory.class)
                .setParameter("batchHistory", batchHistoryRepository.getBatchHistory(batchHistoryId)).getResultList();
    }

    public ArchiveHistory getArchiveHistory(String documentId, String caseId) throws ApplicationException {
        TypedQuery<ArchiveHistory> archiveHistoryList = em.createQuery("SELECT a FROM ArchiveHistory a WHERE a.documentId LIKE :documentId AND a.caseId LIKE :caseId", ArchiveHistory.class)
                .setParameter("documentId", documentId)
                .setParameter("caseId", caseId);

        if (archiveHistoryList.getResultList().size() > 1) {
            throw new ApplicationException("It should not be more than one row in db for documentId: " + documentId + " and caseId: " + caseId);
        } else if (archiveHistoryList.getResultList().size() == 1) {
            return archiveHistoryList.getResultList().get(0);
        } else {
            return null;
        }
    }

    public void postArchiveHistory(ArchiveHistory archiveHistory) {
        em.persist(archiveHistory);
    }

    public void updateArchiveHistory(ArchiveHistory archiveHistory) {
        em.merge(archiveHistory);
    }

}
