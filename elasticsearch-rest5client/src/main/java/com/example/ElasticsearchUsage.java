package com.example;

import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchUsage {

    private final RestClient restClient;

    @Autowired
    public ElasticsearchUsage(RestClient restClient) {
        this.restClient = restClient;
    }

    public RestClient getRestClient() {
        return restClient;
    }
}
