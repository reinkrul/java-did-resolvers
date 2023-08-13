# java-did-resolvers
A set of DID resolvers in Java with minimal dependencies.

# Supported DID methods

## [Web DID Method (did:web)](https://w3c-ccg.github.io/did-method-web/)

Maven dependency:
```xml
<dependency>
    <groupId>nl.reinkrul</groupId>
    <artifactId>did-web-resolver</artifactId>
</dependency>
```

## [JWK DID Method (did:jwk)](https://github.com/quartzjer/did-jwk/blob/main/spec.md) (in progress)

Maven dependency:
```xml
<dependency>
    <groupId>nl.reinkrul</groupId>
    <artifactId>did-jwk-resolver</artifactId>
</dependency>
```

# Compliancy

This library's API follows [W3C DID Core specification v1.0](https://www.w3.org/TR/2022/REC-did-core-20220719/). 