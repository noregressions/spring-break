package com.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "attempt_num")
    private int attemptNum;

    public AuditLog() {
    }

    public AuditLog(String message, int attemptNum) {
        this.message = message;
        this.attemptNum = attemptNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAttemptNum() {
        return attemptNum;
    }

    public void setAttemptNum(int attemptNum) {
        this.attemptNum = attemptNum;
    }
}
