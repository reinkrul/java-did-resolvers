package nl.reinkrul.did.jwk;

import foundation.identity.did.DIDDocument;

public class ResolutionResult {
    private final DIDDocument didDocument;
    private final DIDResolutionMetadata didResolutionMetadata;
    private final DIDDocumentMetadata didDocumentMetadata;

    public ResolutionResult(DIDDocument didDocument, DIDResolutionMetadata didResolutionMetadata, DIDDocumentMetadata didDocumentMetadata) {
        this.didDocument = didDocument;
        this.didResolutionMetadata = didResolutionMetadata;
        this.didDocumentMetadata = didDocumentMetadata;
    }

    public DIDDocument getDidDocument() {
        return didDocument;
    }

    public DIDResolutionMetadata getDidResolutionMetadata() {
        return didResolutionMetadata;
    }

    public DIDDocumentMetadata getDidDocumentMetadata() {
        return didDocumentMetadata;
    }
}
