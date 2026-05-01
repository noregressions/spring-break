# ApacheDS LDAP Support Removed (Tier 1: Won't Compile)

**Summary**: Support for embedded ApacheDS LDAP server has been removed in Spring Security 7.0 in favor of UnboundID.

## What breaks

Code that uses `ApacheDSContainer` to start an embedded LDAP server will fail to compile on Spring Boot 4.0.

```java
import org.springframework.security.ldap.server.ApacheDSContainer;

// ...
new ApacheDSContainer("dc=springframework,dc=org", "classpath:users.ldif");
```

## How this test works

The module includes a class `ApacheDsUsage` that imports and uses `ApacheDSContainer`.

On Boot 3.5: Compiles and runs.
On Boot 4.0: Fails to compile because the class no longer exists.

## Fix / Migration Path

Migrate to UnboundID for embedded LDAP. Spring Security 7.0 provides `UnboundIdContainer` as a replacement.

## References

- [Spring Security 7.0 Migration Guide - LDAP](https://docs.spring.io/spring-security/reference/6.5-SNAPSHOT/migration-7/ldap.html)
- Master list entry: 1.60
