package se.sundsvall.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Provider
@IfBuildProperty(name = "payload.logging.enabled", stringValue = "true")
public class PayloadLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger("COMMUNICATION");
    private static final String BASE64_REGEX = "\\\\*\"(file|htmlMessage|content)\\\\*\"\\s*:\\s*\\\\*\".+\\\\*\"";

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
    public PayloadLoggingFilter(@Context final HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        var info = RequestInfo.builder()
                .withOrigin("remote")
                .withType("request")
                .withRemote(request.remoteAddress().host())
                .withMethod(request.method().name())
                .withUri(request.uri())
                .withHeaders(requestContext.getHeaders())
                .withBody(getEntityBody(requestContext))
                .build();

        LOG.info("Incoming request:\n" + OBJECT_MAPPER.writeValueAsString(info));
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        var info = ResponseInfo.builder()
                .withOrigin("local")
                .withType("response")
                .withStatus(responseContext.getStatus())
                .withHeaders(responseContext.getStringHeaders())
                .withBody(getEntityBody(responseContext.getEntity()))
                .build();

        LOG.info("Outgoing response:\n" + OBJECT_MAPPER.writeValueAsString(info));
    }

    private String getEntityBody(final Object object) {
        String text = "";
        try {
             text = OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error("Error logging request", e);
        }
        // Remove base64 because it takes up so much space
        return text.replaceAll(BASE64_REGEX, "Removed Base64-string");
    }

    private String getEntityBody(final ContainerRequestContext requestContext) {

        try {
            InputStream is = requestContext.getEntityStream();
            byte[] data = is.readAllBytes();
            String body = new String(data, StandardCharsets.UTF_8);

            // Remove base64 because it takes up so much space
            body = body.replaceAll(BASE64_REGEX, "Removed Base64-string");

            requestContext.setEntityStream(new ByteArrayInputStream(data));

            return body;
        } catch (IOException e) {
            LOG.error("Error logging request", e);
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
        private MultivaluedMap<String, String> headers;
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