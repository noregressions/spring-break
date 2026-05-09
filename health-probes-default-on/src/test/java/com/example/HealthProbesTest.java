package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that liveness/readiness probes are off by default on Boot 3.5 and
 * silently on by default on Boot 4.0.
 *
 * Two layers of assertion, each covering a real reader concern:
 *
 * 1. Bean-level: livenessStateHealthIndicator and readinessStateHealthIndicator
 *    are not in the context on Boot 3.5 default; they ARE on Boot 4.0 default.
 *
 * 2. HTTP-level: actually GET the endpoints and check status + body. This is
 *    what downstream consumers (load balancers, monitoring tools, JSON parsers)
 *    see. The /actuator/health body changes shape; /actuator/health/liveness
 *    flips from 404 to 200.
 *
 * Spring Boot 3.5.14 (default — module exposes health endpoint, no platform):
 * - GET /actuator/health → 200 {"status":"UP"}                  (no "groups" key)
 * - GET /actuator/health/liveness → 404                          (group not exposed)
 * - GET /actuator/health/readiness → 404                          (group not exposed)
 * - Beans livenessStateHealthIndicator / readinessStateHealthIndicator absent.
 * - All assertions below pass.
 *
 * Spring Boot 4.0.6 (default — same configuration):
 * - GET /actuator/health → 200 {"groups":["liveness","readiness"],"status":"UP"}
 * - GET /actuator/health/liveness → 200 {"status":"UP"}            (silently reachable)
 * - GET /actuator/health/readiness → 200 {"status":"UP"}           (silently reachable)
 * - Beans livenessStateHealthIndicator / readinessStateHealthIndicator present.
 * - All assertions below fail; the test catches the silent regression.
 *
 * Fix: set management.endpoint.health.probes.enabled=false to restore 3.5 behaviour.
 *      Verified empirically: with that property set on 4.0, the body and status codes
 *      match 3.5 exactly.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthProbesTest {

    @Autowired
    private ApplicationContext context;

    @LocalServerPort
    private int port;

    @Test
    void probeBeansAbsentByDefault() {
        boolean hasLiveness = context.containsBean("livenessStateHealthIndicator");
        boolean hasReadiness = context.containsBean("readinessStateHealthIndicator");

        assertFalse(hasLiveness,
            "livenessStateHealthIndicator bean should NOT be present by default on Boot 3.5. " +
            "On Boot 4.0 it is auto-configured silently — set " +
            "management.endpoint.health.probes.enabled=false to restore 3.5 behaviour.");
        assertFalse(hasReadiness,
            "readinessStateHealthIndicator bean should NOT be present by default on Boot 3.5. " +
            "On Boot 4.0 it is auto-configured silently.");
    }

    @Test
    void probeEndpointsReturn404ByDefault() throws Exception {
        // The real symptom for downstream consumers: the probe endpoints exist or don't.
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> liveness = get(client, "/actuator/health/liveness");
        HttpResponse<String> readiness = get(client, "/actuator/health/readiness");

        assertEquals(404, liveness.statusCode(),
            "/actuator/health/liveness should 404 by default on Boot 3.5 (probe group not exposed). " +
            "On Boot 4.0 it returns 200 — set management.endpoint.health.probes.enabled=false to restore.");
        assertEquals(404, readiness.statusCode(),
            "/actuator/health/readiness should 404 by default on Boot 3.5. " +
            "On Boot 4.0 it returns 200.");
    }

    @Test
    void healthBodyHasNoGroupsKeyByDefault() throws Exception {
        // The Tier 3 silent regression: any consumer parsing /actuator/health JSON sees a new key.
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> health = get(client, "/actuator/health");

        assertEquals(200, health.statusCode());
        assertTrue(
            !health.body().contains("\"groups\""),
            "/actuator/health body should not contain a 'groups' key on Boot 3.5 default. " +
            "On Boot 4.0 the body becomes {\"groups\":[\"liveness\",\"readiness\"],\"status\":\"UP\"} — " +
            "any client parsing the body will see the new field. Body was: " + health.body()
        );
    }

    private HttpResponse<String> get(HttpClient client, String path) throws Exception {
        return client.send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
            HttpResponse.BodyHandlers.ofString());
    }
}
