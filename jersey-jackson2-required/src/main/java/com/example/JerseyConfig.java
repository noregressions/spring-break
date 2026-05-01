package com.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(HelloResource.class);
    }

    @Path("/hello")
    public static class HelloResource {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Message sayHello() {
            return new Message("Hello World");
        }
    }

    public static class Message {
        private String content;
        public Message() {}
        public Message(String content) { this.content = content; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
