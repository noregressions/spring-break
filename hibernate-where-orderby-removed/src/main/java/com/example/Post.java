package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Post {
    @Id
    private Long id;
    private String name;
    private boolean deleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
