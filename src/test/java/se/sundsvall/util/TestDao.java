package se.sundsvall.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class TestDao {

    @Inject
    EntityManager em;

    /**
     * Used for resetting all db-tables in test
     */
    @Transactional
    public void deleteAllFromAllTables() {
        em.createQuery("DELETE FROM ArchiveHistory").executeUpdate();
        em.createQuery("DELETE FROM BatchHistory").executeUpdate();
    }
}
