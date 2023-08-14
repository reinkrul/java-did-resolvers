# java-did-resolvers
A set of DID resolvers in Java with minimal dependencies.

# Supported DID methods

## [Web DID Method (did:web)](https://w3c-ccg.github.io/did-method-web/)

Maven dependency:
```xml
<dependency>
    <groupId>nl.reinkrul.did</groupId>
    <artifactId>did-web-resolver</artifactId>
</dependency>
```

## [JWK DID Method (did:jwk)](https://github.com/quartzjer/did-jwk/blob/main/spec.md) (in progress)

Maven dependency:
```xml
<dependency>
    <groupId>nl.reinkrul.did</groupId>
    <artifactId>did-jwk-resolver</artifactId>
</dependency>
```

# Usage

You can resolve the DID document, yields the parsed DID document:

```java
// this populates didDocument, does not populate didDocumentBytes and contentType
var result = new WebResolver().Resolve(new URI("did:web:example.com"));
System.out.println(result.getDIDDocument().getId());
```

If you want the raw data as `byte[]` (as specified by the DID core specification), you resolve the presentation:

```java
// this populates didDocumentBytes and contentType, does not populate didDocument
var result = new WebResolver().ResolvePresentation(new URI("did:web:example.com"));
var data = result.getDIDDocumentBytes();
// do something with the raw data
```

## JSON-LD

The library supports JSON-LD documents: they will be unmarshalled, but no further JSON-LD processing will be performed. 

# Compliancy

This library's API follows [W3C DID Core specification v1.0](https://www.w3.org/TR/2022/REC-did-core-20220719/). 