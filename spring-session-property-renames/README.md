# Spring Session Property Prefixes Silently Renamed (Tier 3: Different Results)

**Summary**: Spring Boot 4.0 renames Spring Session backend properties:
- `spring.session.redis.*` → `spring.session.data.redis.*`
- `spring.session.mongodb.*` → `spring.session.data.mongodb.*`

The legacy prefixes are silently ignored on Boot 4 — sessions are stored with default namespace/collection names rather than the configured ones. No error, no warning. The most common symptom is "users appear logged out immediately after login" because session keys are written to one namespace and looked up under another.

## What Breaks

| | Boot 3.5.14 | Boot 4.0.6 |
|---|---|---|
| Properties class | `org.springframework.boot.autoconfigure.session.RedisSessionProperties` (in `spring-boot-autoconfigure`) | `org.springframework.boot.session.data.redis.autoconfigure.SessionDataRedisProperties` (in `spring-boot-session-data-redis` — new per-concern jar) |
| `@ConfigurationProperties` prefix | `spring.session.redis` | `spring.session.data.redis` |
| Setting only the legacy prefix | Honored | Silently ignored |

The MongoDB equivalent follows the same pattern (`MongoSessionProperties` → renamed/moved; prefix gains the `data` infix).

Empirically verified by reflecting on the `@ConfigurationProperties` annotation per Boot version.

## How This Test Works

The test loads whichever properties class exists for the running Boot version (Boot 3's `RedisSessionProperties` or Boot 4's `SessionDataRedisProperties`) and reads the `@ConfigurationProperties` `prefix` attribute. It asserts the prefix equals `spring.session.redis` (the Boot-3 canonical value).

Sidesteps the auto-config-firing complexity — Spring Session's auto-config has multiple conditional triggers that aren't trivial to satisfy without a live Redis. The classpath + annotation check proves the rename without needing a working session repository.

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ Test passes. `RedisSessionProperties` is on the classpath; its prefix is `spring.session.redis`.

## On Spring Boot 4.0.6

```bash
mvn test -Dspring-boot.version=4.0.6
```

**Result**: ✗ Test fails.

**Error**:
```
On Boot 3.x the canonical Spring Session Redis property prefix is 'spring.session.redis'.
On Boot 4.0 the prefix has been silently renamed to 'spring.session.data.redis' —
any application.properties using the old prefix is silently ignored.
Found prefix: 'spring.session.data.redis' on class
org.springframework.boot.session.data.redis.autoconfigure.SessionDataRedisProperties
==> expected: <spring.session.redis> but was: <spring.session.data.redis>
```

The failure message names both the new class FQN and the new prefix — exactly what users need for the migration.

## Fix / Migration Path

```diff
- spring.session.redis.namespace=myapp:session
- spring.session.redis.flush-mode=on-save
- spring.session.redis.save-mode=on-set-attribute
+ spring.session.data.redis.namespace=myapp:session
+ spring.session.data.redis.flush-mode=on-save
+ spring.session.data.redis.save-mode=on-set-attribute
```

```diff
- spring.session.mongodb.collection-name=sessions
+ spring.session.data.mongodb.collection-name=sessions
```

`spring.session.store-type` is unchanged. Only the per-store sub-keys have the `data` infix added.

## Watch Out

The failure is silent — sessions ARE stored, just under the default namespace/collection name. A wrong namespace in Redis means session keys go to one place and the lookup checks another, so users appear logged out. Confirm post-migration by inspecting Redis directly (`KEYS '*:session*'`) and verifying keys under the configured namespace.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- See also: `mongodb-property-renames` (the parallel rename for the main MongoDB connection — note the inverse direction: `spring.data.mongodb.*` → `spring.mongodb.*` REMOVES `data`, while session backends ADD it).
