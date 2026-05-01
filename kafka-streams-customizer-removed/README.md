# Kafka Streams Customizer Removed (Tier 1: Won't Compile)

**Summary**: In Spring Boot 4.0, the `StreamsBuilderFactoryBeanCustomizer` interface has been removed. It was previously used to customize the `StreamsBuilderFactoryBean` in Kafka Streams applications. The replacement is `StreamsBuilderFactoryBeanConfigurer`, which is provided by Spring Kafka itself.

## What breaks

On Spring Boot 3.5, `StreamsBuilderFactoryBeanCustomizer` is available in `org.springframework.boot.autoconfigure.kafka`. In Spring Boot 4.0, this interface is removed. Code implementing this interface will fail to compile.

```java
import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer; // Fails on 4.0
```

## How this test works

The module contains:
- `KafkaCustomizerUsage.java`: A component that implements `StreamsBuilderFactoryBeanCustomizer`.
- `KafkaCustomizerTest.java`: A test verifying the customizer is loadable.

On Boot 3.5: The code compiles and the test passes.
On Boot 4.0: Compilation fails because `StreamsBuilderFactoryBeanCustomizer` is no longer available.

## Fix / Migration Path

Migrate to `StreamsBuilderFactoryBeanConfigurer`:

```java
// Before
import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer;
@Bean
public StreamsBuilderFactoryBeanCustomizer customizer() { ... }

// After
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
@Bean
public StreamsBuilderFactoryBeanConfigurer configurer() { ... }
```

## References

- [StreamsBuilderFactoryBeanCustomizer should be deprecated before being abandoned](https://github.com/spring-projects/spring-boot/issues/33819)
- Master list entry: 1.44
