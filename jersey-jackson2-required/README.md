# Jersey Jackson 2 Required (Tier 2: Won't Run)

**Summary**: While Spring Boot 4.0 transitions to Jackson 3 as the default JSON library, `spring-boot-starter-jersey` still includes and requires Jackson 2 for its JSON support.

## What breaks

In Spring Boot 4.0, the core starters (like `spring-boot-starter-web`) have migrated to Jackson 3 (`tools.jackson.*`). However, the Jersey ecosystem's official entity providers (like `jersey-media-json-jackson`) still predominantly support Jackson 2 (`com.fasterxml.jackson.*`).

As a result, applications using Jersey will find Jackson 2 on their classpath even if they intended to move exclusively to Jackson 3. This can lead to:
1. Two versions of Jackson on the classpath.
2. Confusion regarding which `ObjectMapper` is being used or customized.
3. Potential conflicts if Jackson 2 is manually excluded.

## How this test works

The module `jersey-jackson2-required` contains:
- `JerseyConfig.java`: A standard Jersey configuration.
- `JerseyJacksonTest.java`: A test verifying that Jackson 2 classes are present on the classpath.

On Boot 3.5: Jackson 2 is present, and the test passes.
On Boot 4.0: Jackson 2 is STILL present (due to Jersey), which confirms that the full migration to Jackson 3 is not yet possible for Jersey users without custom providers.

## Fix / Migration Path

If you are using Jersey and want to move to Jackson 3, you must:
1. Provide custom EntityProviders for Jersey that use Jackson 3.
2. Be aware that `spring.jackson.*` properties in Spring Boot 4.0 target Jackson 3. Customizations for Jersey's Jackson 2 will likely need to be done programmatically or via `spring.jackson2.*` (if provided by a compatibility module).

## References

- [Introducing Jackson 3 support in Spring](https://spring.io/blog/2025/10/07/introducing-jackson-3-support-in-spring)
- Master list entry: 2.5
