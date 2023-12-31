package nl.reinkrul.did.jwk;

import com.nimbusds.jose.jwk.JWK;
import foundation.identity.did.DIDDocument;
import foundation.identity.did.VerificationMethod;
import nl.reinkrul.did.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * DID resolver for did:jwk DIDs.
 */
public class JWKResolver extends BaseDIDResolver {

    private static final String PREFIX = "did:jwk:";

    @Override
    public ResolutionResult Resolve(URI did, ResolutionOptions resolutionOptions) throws DIDResolutionException {
        var resolutionResult = ResolvePresentation(did, resolutionOptions);
        var didDocument = DIDDocument.fromJson(new String(resolutionResult.getDIDDocumentBytes()));
        return new ResolutionResult(didDocument, null, new DIDResolutionMetadata(null), new DIDDocumentMetadata());
    }

    @Override
    public ResolutionResult ResolvePresentation(URI did, ResolutionOptions resolutionOptions) throws DIDResolutionException {
        validateDID(did, PREFIX);
        byte[] jwkBytes;
        try {
            jwkBytes = Base64.getUrlDecoder().decode(did.toString().substring(PREFIX.length()));
        } catch (IllegalArgumentException e) {
            throw new DIDResolutionException("did:jwk contains invalid base64 encoded ID", e);
        }
        JWK jwk;
        try {
            jwk = JWK.parse(new String(jwkBytes));
        } catch (ParseException e) {
            throw new DIDResolutionException("did:jwk contains invalid JWK", e);
        }

        VerificationMethod verificationMethod;
        URI jwsContextURI;
        try {
            verificationMethod = VerificationMethod.builder()
                    .id(new URI(did + "#0"))
                    .type("JsonWebKey2020")
                    .controller(did)
                    .properties(Map.of("publicKeyJwk", jwk.toJSONObject()))
                    .build();
            jwsContextURI = new URI("https://w3id.org/security/suites/jws-2020/v1");
        } catch (URISyntaxException e) {
            // Impossible
            throw new IllegalArgumentException(e);
        }
        var didDocument = DIDDocument.builder()
                .defaultContexts(true)
                .context(jwsContextURI)
                .id(did)
                .verificationMethod(verificationMethod)
                .properties(Map.of(
                        "assertionMethod", List.of(verificationMethod.getId()),
                        "authentication", List.of(verificationMethod.getId()),
                        "capabilityInvocation", List.of(verificationMethod.getId()),
                        "capabilityDelegation", List.of(verificationMethod.getId()),
                        "keyAgreement", List.of(verificationMethod.getId())
                ))
                .build();
        return new ResolutionResult(
                null, didDocument.toJson().getBytes(),
                new DIDResolutionMetadata("application/did+ld+json"), new DIDDocumentMetadata()
        );
    }
}
