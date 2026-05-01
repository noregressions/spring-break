# Undertow Embedded Server Removed (Tier 1: Won't Launch)

**Summary**: Spring Boot 4.0 removes `spring-boot-starter-undertow`, breaking any application using Undertow as the embedded servlet container.

## What Breaks

Spring Boot 4.0 **completely removes** Undertow as a supported embedded server option. The `spring-boot-starter-undertow` artifact no longer exists in the Boot 4.0 BOM. This is a **Tier 1 failure**: projects using Undertow fail immediately at dependency resolution or runtime.

1. **Artifact removed**: `spring-boot-starter-undertow` does not exist in Spring Boot 4.0
2. **Auto-configuration removed**: All Undertow configuration classes and beans are gone
3. **No replacement available**: Undertow cannot be used in Boot 4.0; no workaround exists

## How This Test Works

The test module demonstrates the Undertow removal by:

- **UndertowApp.java**: A simple Spring Boot REST application with no Undertow-specific code
- **pom.xml**: Explicitly declares `spring-boot-starter-undertow` dependency (for Boot 3.5.14 compatibility)

The application code is generic and works on any embedded server. The breaking change is purely in the dependency configuration.

## On Spring Boot 3.5.14

```bash
mvn clean package
java -jar target/undertow-removed-1.0.0.jar
```

**Result**: ✓ Builds and runs successfully. Undertow starts as the embedded servlet container.

**Output**:
```
Undertow started on port(s) 8080 (http)
```

```bash
curl http://localhost:8080/
# Hello from Undertow embedded server!
```

## On Spring Boot 4.0

```bash
mvn clean package
```

**Result**: ✗ Build fails during dependency resolution.

**Error**:
```
[ERROR] Failed to execute goal on project undertow-removed:
Could not resolve dependencies for project:
Could not find artifact org.springframework.boot:spring-boot-starter-undertow:jar:4.0.0
```

Even if the build somehow succeeds, runtime startup will fail because Undertow auto-configuration doesn't exist.

## Fix / Migration Path

### Option 1: Migrate to Tomcat (Default, Recommended)

Tomcat is the default embedded server in Boot 4.0. Simply remove the Undertow dependency:

```xml
<!-- DELETE THIS -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>

<!-- KEEP THIS (includes Tomcat by default) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

No application code changes required.

### Option 2: Migrate to Jetty

If you prefer Jetty, exclude Tomcat and add Jetty:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <exclusions>
    <exclusion>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-tomcat</artifactId>
    </exclusion>
  </exclusions>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

No application code changes required.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Embedded Servers in Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto.embedded-server)
