# Spring Security Removed APIs (Tier 1: Won't Launch)

**Summary**: Spring Security 7.0 (in Boot 4.0) removes `authorizeRequests()`, `.and()`, and `antMatchers()`, breaking all code using Spring Security 6.x configuration styles.

## What Breaks

Spring Security 7.0 **removes** multiple deprecated APIs that were the standard way to configure Spring Security in 6.x:

1. **authorizeRequests()** removed → replaced with `authorizeHttpRequests()`
2. **.and()** removed → must use lambda DSL closures
3. **antMatchers()** removed → replaced with `requestMatchers()`
4. **formLogin()/logout()** signatures changed to lambda-based configuration

This is a **Tier 1 failure**: code using deprecated APIs fails to compile on Boot 4.0.

## How This Test Works

The test module demonstrates the breaking changes using:

- **SecurityConfig.java**: Uses Spring Security 6.x deprecated APIs (`authorizeRequests()`, `.and()`, `requestMatchers()`)
- **SecurityConfigTest.java**: Tests that authorization rules are properly applied
- **SecurityApp.java**: Simple REST endpoints with different security requirements

## On Spring Boot 3.5.14

```bash
mvn clean compile
```

**Result**: ✓ Compiles successfully (with deprecation warnings).

```bash
mvn spring-boot:run
```

**Result**: ✓ Runs successfully. Security rules are enforced:
- `/public/**` — accessible without authentication
- `/protected/**` — requires authentication
- `/admin/**` — requires ADMIN role

## On Spring Boot 4.0

```bash
mvn clean compile
```

**Result**: ✗ Compilation fails with multiple errors:

```
[ERROR] error: cannot find symbol: method authorizeRequests()
[ERROR] error: cannot find symbol: method and()
[ERROR] error: cannot find symbol: method antMatchers(String)
```

## Fix / Migration Path

### 1. Replace authorizeRequests() with authorizeHttpRequests()

```java
// OLD (deprecated in 6.x, removed in 7.0)
.authorizeRequests()
    .requestMatchers("/public/**").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
.and()

// NEW (Spring Security 7.0)
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/public/**").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

### 2. Remove .and() Chaining

Replace `.and()` with lambda closures. Each feature method (formLogin, logout, etc.) takes a lambda:

```java
// OLD (with .and() chaining)
.authorizeRequests()
    .anyRequest().authenticated()
.and()
    .formLogin()
        .loginPage("/login")
        .permitAll()
.and()
    .logout()
        .permitAll()

// NEW (lambda-based DSL)
.authorizeHttpRequests(authz -> authz
    .anyRequest().authenticated()
)
.formLogin(login -> login
    .loginPage("/login")
    .permitAll()
)
.logout(logout -> logout
    .permitAll()
)
```

### 3. Update antMatchers() to requestMatchers()

`antMatchers()` was removed; use `requestMatchers()` instead:

```java
// OLD
.antMatchers("/admin/**").hasRole("ADMIN")
.antMatchers(HttpMethod.POST, "/api/**").authenticated()

// NEW
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.POST, "/api/**").authenticated()
```

### 4. Complete Example

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }
}
```

## References

- [Spring Security 7.0 Migration Guide](https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html)
- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Framework 7 What's New](https://github.com/spring-projects/spring-framework/wiki/What%27s-New-in-Spring-Framework-7.x)
