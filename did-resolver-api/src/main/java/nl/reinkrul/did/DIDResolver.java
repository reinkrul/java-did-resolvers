package nl.reinkrul.did;

import java.net.URI;

/**
 * Defines an interface for DID resolvers to implement.
 */
public interface DIDResolver {
    /**
     * Resolves a DID, parsing the result into a DIDDocument.
     * @param did The DID to resolve.
     * @param resolutionOptions The options to use when resolving the DID.
     * @return The resolution result with the DIDDocument.
     * @throws InterruptedException If the resolution is interrupted.
     * @throws DIDResolutionException If the resolution fails.
     */
    ResolutionResult Resolve(URI did, ResolutionOptions resolutionOptions) throws InterruptedException, DIDResolutionException;

    /**
     * Resolves a DID, returning the raw bytes of the DID document and its content type.
     * @param did The DID to resolve.
     * @param resolutionOptions The options to use when resolving the DID.
     * @return The resolution result with the DIDDocument.
     * @throws InterruptedException If the resolution is interrupted.
     * @throws DIDResolutionException If the resolution fails.
     */
    ResolutionResult ResolvePresentation(URI did, ResolutionOptions resolutionOptions) throws InterruptedException, DIDResolutionException;
}
