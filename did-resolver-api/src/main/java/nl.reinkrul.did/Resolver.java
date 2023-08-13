package nl.reinkrul.did;

import java.io.IOException;
import java.net.URI;

public interface Resolver {
    ResolutionResult Resolve(URI did, ResolutionOptions resolutionOptions) throws IOException, InterruptedException;

    ResolutionResult ResolvePresentation(URI did, ResolutionOptions resolutionOptions) throws IOException, InterruptedException;
}
