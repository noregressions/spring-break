# HttpMessageConverters Deprecated (Tier 2: Won't Run)

**Summary**: The `HttpMessageConverters` bean and its auto-configuration have been deprecated/removed in Spring Boot 4.0. Users should migrate to `ClientHttpMessageConvertersCustomizer` and `ServerHttpMessageConvertersCustomizer` for customizing converters.

## What breaks

In Spring Boot 3.5, you can define a bean of type `org.springframework.boot.autoconfigure.http.HttpMessageConverters` to provide or supplement the message converters used by `RestTemplate` and Spring MVC.

In Spring Boot 4.0, `HttpMessageConvertersAutoConfiguration` has been removed from the default auto-configuration list. While the `HttpMessageConverters` class might still exist (likely deprecated), simply defining a bean of this type will no longer automatically configure converters for the application.

```java
// Spring Boot 3.5 (Works)
@Bean
public HttpMessageConverters customConverters() {
    return new HttpMessageConverters(new MyCustomConverter());
}

// Spring Boot 4.0 (Ignored or Fails to Inject)
// The bean is no longer processed by auto-configuration.
```

## How this test works

The module `httpmessageconverters-deprecated` contains:
- `ConverterApp.java`: Defines an `HttpMessageConverters` bean.
- `ConverterTest.java`: A test that asserts the bean is injected into the test context.

On Boot 3.5: The bean is injected, and the test passes.
On Boot 4.0: `HttpMessageConvertersAutoConfiguration` is missing, so the bean is not auto-processed or recognized by the infrastructure. The test fails with a null assertion.

## Fix / Migration Path

Migrate to using the new customizer interfaces:
- `ServerHttpMessageConvertersCustomizer` for Spring MVC / server-side.
- `ClientHttpMessageConvertersCustomizer` for `RestClient` / `RestTemplate`.

```java
// Spring Boot 4.0 (Recommended Fix)
@Bean
public ServerHttpMessageConvertersCustomizer serverCustomizer() {
    return (converters) -> converters.add(new MyCustomConverter());
}
```

## References

- [Spring Boot 4 removed HttpMessageConvertersAutoConfiguration](https://stackoverflow.com/questions/79892049/spring-boot-4-removed-httpmessageconvertersautoconfiguration-recommended-migra)
- Master list entry: 2.3
