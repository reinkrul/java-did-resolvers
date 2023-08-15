package nl.reinkrul.did;

public class DIDResolutionMetadata {
    private final String contentType;

    public DIDResolutionMetadata(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
