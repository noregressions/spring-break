package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.OrderBy;
import java.util.List;

@Entity
public class User {
    @Id
    private Long id;
    private String name;

    @OneToMany
    @Where(clause = "deleted = false") // Removed in Hibernate 7.0
    @OrderBy(clause = "name desc")     // Removed in Hibernate 7.0
    private List<Post> posts;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
}
