package nl.reinkrul.did.web;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class WebResolverIntegrationTest {

    private static HttpServer server;

    @BeforeAll
    public static void setup() throws IOException {
        int port;
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
        }
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            try (InputStream inputStream = WebResolverIntegrationTest.class.getResourceAsStream("/did.json")) {
                var data = new String(inputStream.readAllBytes());
                data = data.replaceAll(Pattern.quote("example.com"), "localhost%3A" + server.getAddress().getPort());
                var responseStream = exchange.getResponseBody();
                responseStream.write(data.getBytes(StandardCharsets.UTF_8));
                responseStream.flush();
                responseStream.close();
            }
            exchange.close();
        });
        server.setExecutor(null);
        server.start();
    }

    @AfterAll
    public static void tearDown() {
        server.stop(0);
    }

    @Test
    public void resolve() throws URISyntaxException, IOException, InterruptedException {
        var resolver = new WebResolver(HttpClient.newHttpClient(), "http");

        var result = resolver.Resolve(new URI("did:web:localhost%3A" + server.getAddress().getPort()), null);

        assertNotNull(result);
        assertEquals("did:web:localhost%3A" + server.getAddress().getPort(), result.getDIDDocument().getId().toString());
    }
}