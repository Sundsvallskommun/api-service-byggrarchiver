package se.sundsvall.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.arc.properties.IfBuildProperty;
import io.vertx.core.http.HttpServerRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

//@Provider
//@IfBuildProperty(name = "client.logging.enabled", stringValue = "true")
public class ClientLoggingFilter implements ClientRequestFilter, ClientResponseFilter {

    private static final Logger LOG = Logger.getLogger("CLIENT-COMMUNICATION");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            .registerModule(new JavaTimeModule());

    private final HttpServerRequest request;

    @Inject
    public ClientLoggingFilter(@Context final HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        var info = RequestInfo.builder()
                .withOrigin("client")
                .withType("request")
                .withMethod(requestContext.getMethod())
                .withUri(requestContext.getUri().toString())
                .withHeaders(requestContext.getHeaders())
                .withBody(requestContext.getEntity())
                .build();

        LOG.info("Client request:\n" + OBJECT_MAPPER.writeValueAsString(info));
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext, ClientResponseContext responseContext) throws IOException {
        var info = ResponseInfo.builder()
                .withOrigin("server")
                .withType("response")
                .withStatus(responseContext.getStatus())
                .withHeaders(responseContext.getHeaders())
                .withBody(getResponseEntityBody(responseContext))
                .build();

        LOG.info("Client response:\n" + OBJECT_MAPPER.writeValueAsString(info));
    }

    private String getResponseEntityBody(final ClientResponseContext responseContext) {

        try {
            InputStream is = responseContext.getEntityStream();
            byte[] data = is.readAllBytes();
            String body = new String(data, StandardCharsets.UTF_8);
            responseContext.setEntityStream(new ByteArrayInputStream(data));

            return body;
        } catch (IOException e) {
            LOG.error("Error logging response", e);
        }

        return null;
    }

    @Getter
    @Builder(setterPrefix = "with")
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @JsonPropertyOrder({"origin", "type", "remote", "method", "uri", "headers", "body"})
    public static class RequestInfo {

        private String origin;
        private String type;
        private String remote;
        private String method;
        private String uri;
        private MultivaluedMap<String, Object> headers;
        private Object body;
    }

    @Getter
    @Builder(setterPrefix = "with")
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @JsonPropertyOrder({"origin", "type", "status", "headers", "body"})
    public static class ResponseInfo {

        private String origin;
        private String type;
        private int status;
        private MultivaluedMap<String, String> headers;
        private Object body;
    }
}