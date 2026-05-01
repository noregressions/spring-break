package com.example;

import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

/**
 * Service that returns a ListenableFuture.
 *
 * Spring Boot 3.5: ListenableFuture exists in spring-core, compiles fine.
 * Spring Boot 4.0: ListenableFuture deleted from Spring Framework 7, compilation fails.
 *
 * Fix: Replace ListenableFuture with java.util.concurrent.CompletableFuture.
 */
@Service
public class AsyncService {

    public ListenableFuture<String> fetchData() {
        SettableListenableFuture<String> future = new SettableListenableFuture<>();
        future.set("data-from-service");
        return future;
    }
}
