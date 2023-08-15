package nl.reinkrul.did.web;

import foundation.identity.did.DIDDocument;
import nl.reinkrul.did.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WebResolver extends BaseDIDResolver {

    private static final String PREFIX = "did:web:";
    private final HttpClient httpClient;
    private final String protocol;

    public WebResolver() {
        this(HttpClient.newBuilder().build());
    }

    public WebResolver(HttpClient httpClient) {
        this(httpClient, "https");
    }

    protected WebResolver(HttpClient httpClient, String protocol) {
        this.httpClient = httpClient;
        this.protocol = protocol;
    }

    @Override
    public ResolutionResult Resolve(URI did, ResolutionOptions resolutionOptions) throws InterruptedException, DIDResolutionException {
        var resolutionResult = ResolvePresentation(did, resolutionOptions);
        var contentType = resolutionResult.getDIDResolutionMetadata().getContentType();
        // Up until the first ; (could contain parameters, e.g. charset=utf-8)
        if (contentType.contains(";")) {
            contentType = contentType.substring(0, contentType.indexOf(';'));
        }
        var result = switch (contentType) {
            case "application/did+json", "application/did+ld+json", "application/json" -> {
                var didDocument = DIDDocument.fromJson(new String(resolutionResult.getDIDDocumentBytes()));
                yield new ResolutionResult(
                        didDocument, null, new DIDResolutionMetadata(null),
                        resolutionResult.getDIDDocumentMetadata()
                );
            }
            default -> throw new DIDResolutionException("did:web DID resolve returned unsupported Content-Type: " + contentType);
        };
        if (!did.equals(result.getDIDDocument().getId())) {
            throw new DIDResolutionException("did:web resolved DID document with different ID: " + result.getDIDDocument().getId() + " (expected: " + did + ")");
        }
        return result;
    }

    @Override
    public ResolutionResult ResolvePresentation(URI did, ResolutionOptions resolutionOptions) throws InterruptedException, DIDResolutionException {
        validateDID(did, PREFIX);
        var didStr = did.toString();
        if (didStr.contains("/")) {
            // Invalid path-encoding, needs to be done with semicolons.
            throw new DIDResolutionException("did:web contains invalid path-separators");
        }
        // Strip prefix
        didStr = didStr.substring(PREFIX.length());
        // Replace colons with slashes
        didStr = didStr.replace(':', '/');
        // Decode percent-encoded characters, allowed by DID core spec
        didStr = URLDecoder.decode(didStr, StandardCharsets.UTF_8);
        // Check for resulting empty paths (DID ending with semicolon or with double semicolon)
        if (didStr.endsWith("/") || didStr.contains("//")) {
            throw new DIDResolutionException("did:web contains empty path-segments");
        }

        var url = protocol + "://" + didStr;
        if (didStr.contains("/")) {
            // Sub path, append did.json
            url += "/did.json";
        } else {
            // No path, use well-known
            url += "/.well-known/did.json";
        }

        final HttpRequest httpRequest;
        try {
            httpRequest = HttpRequest.newBuilder().GET().uri(new URI(url)).build();
        } catch (URISyntaxException e) {
            throw new DIDResolutionException("did:web translates to invalid URI", e);
        }
        HttpResponse<byte[]> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new DIDResolutionException("did:web DID resolve failed", e);
        }
        if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
            throw new DIDResolutionException("did:web DID resolve returned non-OK status code: " + httpResponse.statusCode());
        }
        var contentType = httpResponse.headers().firstValue("Content-Type");
        if (contentType.isEmpty()) {
            throw new DIDResolutionException("did:web DID resolve returned no Content-Type");
        }
        return new ResolutionResult(null, httpResponse.body(), new DIDResolutionMetadata(contentType.get()), new DIDDocumentMetadata());
    }
}
