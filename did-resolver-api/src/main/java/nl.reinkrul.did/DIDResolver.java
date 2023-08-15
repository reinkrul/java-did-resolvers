package nl.reinkrul.did;

import java.io.IOException;
import java.net.URI;

public interface DIDResolver {
    ResolutionResult Resolve(URI did, ResolutionOptions resolutionOptions) throws InterruptedException, DIDResolutionException;

    ResolutionResult ResolvePresentation(URI did, ResolutionOptions resolutionOptions) throws InterruptedException, DIDResolutionException;
}
