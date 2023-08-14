package nl.reinkrul.did.jwk;

import nl.reinkrul.did.DIDResolutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JWKResolverTest {

    @Test
    void resolve() throws URISyntaxException, InterruptedException, DIDResolutionException {
        var did = new URI("did:jwk:eyJjcnYiOiJQLTI1NiIsImt0eSI6IkVDIiwieCI6ImFjYklRaXVNczNpOF91c3pFakoydHBUdFJNNEVVM3l6OTFQSDZDZEgyVjAiLCJ5IjoiX0tjeUxqOXZXTXB0bm1LdG00NkdxRHo4d2Y3NEk1TEtncmwyR3pIM25TRSJ9");

        var result = new JWKResolver().Resolve(did, null);
        System.out.println(result.getDIDDocument().toJson(true));

        var vm = result.getDIDDocument().getAllVerificationMethods().get(0);
        assertEquals(new URI(did + "#0"), vm.getId());
        assertEquals("JsonWebKey2020", vm.getType());
        assertEquals(did, vm.getController());

        var jwk = vm.getPublicKeyJwk();
        assertEquals("P-256", jwk.get("crv"));
        assertEquals("EC", jwk.get("kty"));
    }

    @Test
    void resolve_InvalidBase64() throws URISyntaxException {
        var did = new URI("did:jwk:..");

        var ex = assertThrows(DIDResolutionException.class, () -> new JWKResolver().Resolve(did, null));

        assertEquals("did:jwk contains invalid base64 encoded ID", ex.getMessage());
    }

    @Test
    void resolve_InvalidJWK() throws URISyntaxException {
        var did = new URI("did:jwk:" + Base64.getUrlEncoder().encodeToString("\"invalid\"".getBytes()));

        var ex = assertThrows(DIDResolutionException.class, () -> new JWKResolver().Resolve(did, null));

        assertEquals("did:jwk contains invalid JWK", ex.getMessage());
    }
}