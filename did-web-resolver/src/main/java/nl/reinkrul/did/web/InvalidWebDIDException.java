package nl.reinkrul.did.web;

import java.io.IOException;

public class InvalidWebDIDException extends IOException {

    public InvalidWebDIDException() {
        super("Invalid did:web DID");
    }
}
