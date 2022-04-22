package se.sundsvall.byggrarchiver.integration.db;

import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class BatchHistoryRepository {

    @Inject
    EntityManager em;

    public List<BatchHistory> getBatchHistories() {
        TypedQuery<BatchHistory> batchHistoryList = em.createQuery("SELECT a FROM BatchHistory a", BatchHistory.class);

        return batchHistoryList.getResultList();
    }

    public void postBatchHistory(BatchHistory batchHistory) {
        em.persist(batchHistory);
    }

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
