package se.sundsvall.byggrarchiver.integration.arendeexport;

import io.quarkiverse.cxf.annotation.CXFClient;
import se.tekis.arende.Dokument;
import se.tekis.servicecontract.ArendeBatch;
import se.tekis.servicecontract.BatchFilter;
import se.tekis.servicecontract.IExportArenden;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ArendeExportIntegrationClient {

    @Inject
    @CXFClient("ARENDEEXPORT")
    IExportArenden iExportArenden;

    public ArendeBatch getUpdatedArenden(BatchFilter filter) {
        return iExportArenden.getUpdatedArenden(filter);
    }

    public List<Dokument> getDocument(String dokId) {
        return iExportArenden.getDocument(dokId, true, null);
    }
}
