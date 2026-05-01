# Elasticsearch RestClient Renamed (Tier 1: Won't Compile)

**Summary**: In Spring Boot 4.0, the low-level Elasticsearch `RestClient` auto-configuration has been replaced with `Rest5Client`. This aligns with the evolution of the Elasticsearch client library. Applications that manually configure or inject the low-level client must update their code to use `Rest5Client`.

## What breaks

On Spring Boot 3.5, the low-level client is `org.elasticsearch.client.RestClient`. In Spring Boot 4.0, Spring Data Elasticsearch has introduced `Rest5Client` as the low-level client type. Code specifically looking for a `RestClient` bean or using `RestClientBuilderCustomizer` will fail to compile or find the bean.

```java
import org.elasticsearch.client.RestClient; // Class still exists, but Boot 4.0 prefers Rest5Client for auto-config
```

## How this test works

The module contains:
- `ElasticsearchUsage.java`: A component that attempts to inject `org.elasticsearch.client.RestClient`.
- `ElasticsearchRestClientTest.java`: A test verifying the injection.

On Boot 3.5: `RestClient` is auto-configured and injected successfully.
On Boot 4.0: Spring Boot 4.0 auto-configures `Rest5Client` instead.

## Fix / Migration Path

Migrate from `RestClient` to `Rest5Client` and update related customizers:

```java
// Before
import org.elasticsearch.client.RestClient;
@Autowired RestClient restClient;

// After
import org.elasticsearch.client.Rest5Client;
@Autowired Rest5Client restClient;
```

## References

- [Auto-configure Elasticsearch's new Rest5Client rather than the legacy RestClient](https://github.com/spring-projects/spring-boot/issues/46061)
- Master list entry: 1.46
