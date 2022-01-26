package se.sundsvall.integration.support.transformers;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import org.apache.commons.lang3.StringUtils;

public class GetDocumentTransformer extends ResponseTransformer {
    @Override
    public Response transform(Request request, Response response, FileSource fileSource, Parameters parameters) {

            String requestBody = request.getBodyAsString();
            String text = StringUtils.substringBetween(requestBody, "<documentId>", "</documentId>");

            String newResponse = response.getBodyAsString().replaceFirst("dokId=\"dokId\"", "dokId=\""+ text +"\"");

            return Response.Builder.like(response)
                    .but().body(newResponse)
                    .build();
    }

    @Override
    public String getName() {
        return "get-document-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

}
