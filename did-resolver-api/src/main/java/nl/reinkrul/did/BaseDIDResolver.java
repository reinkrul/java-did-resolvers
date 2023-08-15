package nl.reinkrul.did;

import java.net.URI;

public abstract class BaseDIDResolver implements DIDResolver {

    protected void validateDID(URI did, String prefix) throws DIDResolutionException {
        if (did.getFragment() != null) {
            throw new DIDResolutionException("DID should not contain a URL fragment");
        }
        if (did.getQuery() != null) {
            throw new DIDResolutionException("DID should not contain a URL query");
        }
        if (did.getPath() != null) {
            throw new DIDResolutionException("DID should not contain a URL path");
        }
        if (!did.toString().startsWith(prefix)) {
            throw new DIDResolutionException("DID does not start with " + prefix);
        }
    }
}
