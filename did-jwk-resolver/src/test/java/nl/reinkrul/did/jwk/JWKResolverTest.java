package nl.reinkrul.did.jwk;

import nl.reinkrul.did.DIDResolutionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

class JWKResolverTest {

    @Test
    void resolve() throws URISyntaxException, IOException, InterruptedException, DIDResolutionException {
        var did = "did:jwk:eyJjcnYiOiJQLTI1NiIsImt0eSI6IkVDIiwieCI6ImFjYklRaXVNczNpOF91c3pFakoydHBUdFJNNEVVM3l6OTFQSDZDZEgyVjAiLCJ5IjoiX0tjeUxqOXZXTXB0bm1LdG00NkdxRHo4d2Y3NEk1TEtncmwyR3pIM25TRSJ9";
        var result = new JWKResolver().Resolve(new URI(did), null);

        System.out.println(result.getDIDDocument().toJson(true));
    }

    @Test
    void resolvePresentation() {
    }
}