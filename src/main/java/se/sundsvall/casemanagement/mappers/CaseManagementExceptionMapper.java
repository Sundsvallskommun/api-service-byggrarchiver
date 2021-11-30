package se.sundsvall.casemanagement.mappers;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ServiceException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class CaseManagementExceptionMapper implements ResponseExceptionMapper<ServiceException> {

    @Inject
    Logger log;

    @Override
    public ServiceException toThrowable(Response response) {
        String responseBody = response.readEntity(String.class);
        log.info("Response from CaseManagement -->\nHTTP Status: " + response.getStatusInfo().getStatusCode() + " " + response.getStatusInfo().getReasonPhrase() + "\nResponse body: " + responseBody);

        return ServiceException.create(responseBody, null, response.getStatusInfo().toEnum());
    }

}
