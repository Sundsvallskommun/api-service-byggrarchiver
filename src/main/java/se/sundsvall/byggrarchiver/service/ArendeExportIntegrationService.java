package se.sundsvall.byggrarchiver.service;

import org.jboss.logging.Logger;
import se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportIntegrationClient;
import se.sundsvall.byggrarchiver.service.exceptions.ExternalServiceException;
import se.tekis.arende.Dokument;
import se.tekis.servicecontract.ArendeBatch;
import se.tekis.servicecontract.BatchFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ArendeExportIntegrationService {
    @Inject
    Logger log;
    @Inject
    ArendeExportIntegrationClient arendeExportIntegrationClient;

    public ArendeBatch getUpdatedArenden(BatchFilter filter) {
        try {
            return arendeExportIntegrationClient.getUpdatedArenden(filter);
        } catch (Exception e) {
            log.error("Request to arendeExportIntegrationService.getUpdatedArenden() failed.", e);
            throw new ExternalServiceException();
        }
    }

    public List<Dokument> getDocument(String dokId) {
        try {
            return arendeExportIntegrationClient.getDocument(dokId);
        } catch (Exception e) {
            log.error("Request to arendeExportIntegrationService.getDocument(" + dokId + ") failed.", e);
            throw new ExternalServiceException();
        }
    }


}
