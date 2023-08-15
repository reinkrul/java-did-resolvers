package nl.reinkrul.did.web;

import nl.reinkrul.did.DIDResolutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebResolverTest {

    private static URI exampleDID() throws URISyntaxException {
        return new URI("did:web:example.com");
    }

    private static URI exampleSubPathDID() throws URISyntaxException {
        return new URI("did:web:example.com:level1:level2");
    }

    private static byte[] exampleDocument() throws URISyntaxException, DIDResolutionException, IOException {
        try (var stream = WebResolverTest.class.getResourceAsStream("/did.json")) {
            return stream.readAllBytes();
        }
    }

    private void mockResponse(byte[] document, int statusCode, String contentType) throws DIDResolutionException, InterruptedException, IOException {
        HttpHeaders headers;
        if (contentType == null) {
            headers = HttpHeaders.of(Map.of(), (s1, s2) -> true);
        } else {
            headers = HttpHeaders.of(Map.of("Content-Type", List.of(contentType)), (s1, s2) -> true);
        }
        when(httpClient.<byte[]>send(httpRequest.capture(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(statusCode);
        when(httpResponse.body()).thenReturn(document);
        when(httpResponse.headers()).thenReturn(headers);
    }

    @Mock
    HttpClient httpClient;

    @Mock(strictness = Mock.Strictness.LENIENT)
    HttpResponse<byte[]> httpResponse;

    @Captor
    ArgumentCaptor<HttpRequest> httpRequest;

    @Test
    void resolve_ApplicationJson() throws URISyntaxException, InterruptedException, DIDResolutionException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/json");
        var resolver = new WebResolver(httpClient);

        var result = resolver.Resolve(exampleDID(), null);

        assertNull(result.getDIDDocumentBytes());
        assertNull(result.getDIDResolutionMetadata().getContentType());
        assertEquals(exampleDID(), result.getDIDDocument().getId());
    }

    @Test
    void resolve_ApplicationJsonWithParameter() throws URISyntaxException, InterruptedException, DIDResolutionException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/json;charset=utf-8");
        var resolver = new WebResolver(httpClient);

        var result = resolver.Resolve(exampleDID(), null);

        assertNull(result.getDIDDocumentBytes());
        assertNull(result.getDIDResolutionMetadata().getContentType());
        assertEquals(exampleDID(), result.getDIDDocument().getId());
    }

    @Test
    void resolve_ApplicationDidJson() throws URISyntaxException, InterruptedException, DIDResolutionException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/did+json");
        var resolver = new WebResolver(httpClient);

        var result = resolver.Resolve(exampleDID(), null);

        assertNull(result.getDIDDocumentBytes());
        assertNull(result.getDIDResolutionMetadata().getContentType());
        assertEquals(exampleDID(), result.getDIDDocument().getId());
    }

    @Test
    void resolve_ApplicationDidLdJson() throws URISyntaxException, InterruptedException, DIDResolutionException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/did+ld+json");
        var resolver = new WebResolver(httpClient);

        var result = resolver.Resolve(exampleDID(), null);

        assertNull(result.getDIDDocumentBytes());
        assertNull(result.getDIDResolutionMetadata().getContentType());
        assertEquals(exampleDID(), result.getDIDDocument().getId());
    }

    @Test
    void resolve_UnsupportedContentType() throws URISyntaxException, DIDResolutionException, InterruptedException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "text/plain");
        var resolver = new WebResolver(httpClient);

        var ex = assertThrows(DIDResolutionException.class, () -> resolver.Resolve(exampleDID(), null));

        assertEquals("did:web DID resolve returned unsupported Content-Type: text/plain", ex.getMessage());
    }

    @Test
    void resolve_DocumentIDDiffers() throws URISyntaxException, DIDResolutionException, InterruptedException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/json");
        var resolver = new WebResolver(httpClient);

        var ex = assertThrows(DIDResolutionException.class, () -> resolver.Resolve(new URI("did:web:example.com:subpath"), null));

        assertEquals("did:web resolved DID document with different ID: did:web:example.com (expected: did:web:example.com:subpath)", ex.getMessage());
    }

    @Test
    void resolvePresentation_WellKnown() throws URISyntaxException, InterruptedException, DIDResolutionException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/json");
        var resolver = new WebResolver(httpClient);

        var result = resolver.ResolvePresentation(exampleDID(), null);

        assertNull(result.getDIDDocument());
        assertEquals(document, result.getDIDDocumentBytes());
        assertEquals("application/json", result.getDIDResolutionMetadata().getContentType());
        assertEquals("https://example.com/.well-known/did.json", httpRequest.getValue().uri().toString());
    }

    @Test
    void resolvePresentation_WellKnown_Port() throws URISyntaxException, InterruptedException, DIDResolutionException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/json");
        var resolver = new WebResolver(httpClient);

        var result = resolver.ResolvePresentation(new URI("did:web:example.com%3A8080"), null);

        assertNull(result.getDIDDocument());
        assertEquals(document, result.getDIDDocumentBytes());
        assertEquals("application/json", result.getDIDResolutionMetadata().getContentType());
        assertEquals("https://example.com:8080/.well-known/did.json", httpRequest.getValue().uri().toString());
        assertEquals(8080, httpRequest.getValue().uri().getPort());
    }

    @Test
    void resolvePresentation_SubPath() throws URISyntaxException, InterruptedException, DIDResolutionException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/json");
        var resolver = new WebResolver(httpClient);

        var result = resolver.ResolvePresentation(exampleSubPathDID(), null);

        assertNull(result.getDIDDocument());
        assertEquals(document, result.getDIDDocumentBytes());
        assertEquals("application/json", result.getDIDResolutionMetadata().getContentType());
        assertEquals("https://example.com/level1/level2/did.json", httpRequest.getValue().uri().toString());
    }

    @Test
    void resolvePresentation_SubPath_Port() throws URISyntaxException, InterruptedException, DIDResolutionException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, "application/json");
        var resolver = new WebResolver(httpClient);

        var result = resolver.ResolvePresentation(new URI("did:web:example.com%3A8080:level1:level2"), null);

        assertNull(result.getDIDDocument());
        assertEquals(document, result.getDIDDocumentBytes());
        assertEquals("application/json", result.getDIDResolutionMetadata().getContentType());
        assertEquals("https://example.com:8080/level1/level2/did.json", httpRequest.getValue().uri().toString());
    }

    @Test
    void resolvePresentation_NotAWebDID() {
        var resolver = new WebResolver(httpClient);

        assertThrows(DIDResolutionException.class, () -> resolver.ResolvePresentation(new URI("did:example:123"), null));
    }

    @Test
    void resolvePresentation_IncorrectPathEncoding() {
        var resolver = new WebResolver(httpClient);

        assertThrows(DIDResolutionException.class, () -> resolver.ResolvePresentation(new URI("did:web:example.com/subpath"), null));
    }

    @Test
    void resolvePresentation_EmptySubPathPostfix() {
        var resolver = new WebResolver(httpClient);

        assertThrows(DIDResolutionException.class, () -> resolver.ResolvePresentation(new URI("did:web:example.com:"), null));
    }

    @Test
    void resolvePresentation_EmptySubPathInBetween() {
        var resolver = new WebResolver(httpClient);

        assertThrows(DIDResolutionException.class, () -> resolver.ResolvePresentation(new URI("did:web:example.com::subpath"), null));
    }

    @Test
    void resolvePresentation_NoContentType() throws URISyntaxException, DIDResolutionException, InterruptedException, IOException {
        var document = exampleDocument();
        mockResponse(document, 200, null);
        var resolver = new WebResolver(httpClient);

        var ex = assertThrows(DIDResolutionException.class, () -> resolver.ResolvePresentation(exampleSubPathDID(), null));

        assertEquals("did:web DID resolve returned no Content-Type", ex.getMessage());
    }

    @Test
    void resolvePresentation_NonOKStatusCode() throws DIDResolutionException, InterruptedException, IOException {
        mockResponse(null, 404, null);
        var resolver = new WebResolver(httpClient);

        var ex = assertThrows(DIDResolutionException.class, () -> resolver.ResolvePresentation(exampleDID(), null));

        assertEquals("did:web DID resolve returned non-OK status code: 404", ex.getMessage());
    }
}