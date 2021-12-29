package se.sundsvall;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class TestDao {

    @Inject
    EntityManager em;

    @Transactional
    void deleteAllFromAllTables() {
        em.createQuery("DELETE FROM ArchiveHistory").executeUpdate();
        em.createQuery("DELETE FROM BatchHistory").executeUpdate();
    }
}
