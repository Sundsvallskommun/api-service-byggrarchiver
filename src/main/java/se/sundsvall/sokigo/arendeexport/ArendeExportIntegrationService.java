package se.sundsvall.sokigo.arendeexport;

import io.quarkiverse.cxf.annotation.CXFClient;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import se.tekis.arende.Dokument;
import se.tekis.servicecontract.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ArendeExportIntegrationService {

    @Inject
    @CXFClient("ARENDEEXPORT")
    IExportArenden iExportArenden;

    @PostConstruct
    void configureClient() {
        Client client = ClientProxy.getClient(iExportArenden);

//        LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
//        loggingInInterceptor.setPrettyLogging(true);
        //        client.getInInterceptors().add(loggingInInterceptor);
        LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
        loggingOutInterceptor.setPrettyLogging(true);

        client.getOutInterceptors().add(loggingOutInterceptor);
    }

    public Arende getArende(String dnr) {
        return iExportArenden.getArende(dnr);
    }

    public ArendeBatch getUpdatedArenden(BatchFilter filter) {
        return iExportArenden.getUpdatedArenden(filter);
    }

    public List<Dokument> getDocument(String dokId) {
        return iExportArenden.getDocument(dokId, true, null);
    }
}
