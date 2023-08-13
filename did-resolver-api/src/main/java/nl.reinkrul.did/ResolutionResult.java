package nl.reinkrul.did;

import foundation.identity.did.DIDDocument;

public class ResolutionResult {
    private final byte[] didDocumentBytes;
    private final DIDDocument didDocument;
    private final DIDResolutionMetadata didResolutionMetadata;
    private final DIDDocumentMetadata didDocumentMetadata;

    public ResolutionResult(DIDDocument didDocument, byte[] didDocumentBytes, DIDResolutionMetadata didResolutionMetadata, DIDDocumentMetadata didDocumentMetadata) {
        this.didDocumentBytes = didDocumentBytes;
        this.didDocument = didDocument;
        this.didResolutionMetadata = didResolutionMetadata;
        this.didDocumentMetadata = didDocumentMetadata;
    }

    public byte[] getDIDDocumentBytes() {
        return didDocumentBytes;
    }

    public DIDDocument getDIDDocument() {
        return didDocument;
    }

    public DIDResolutionMetadata getDIDResolutionMetadata() {
        return didResolutionMetadata;
    }

    public DIDDocumentMetadata getDIDDocumentMetadata() {
        return didDocumentMetadata;
    }
}
