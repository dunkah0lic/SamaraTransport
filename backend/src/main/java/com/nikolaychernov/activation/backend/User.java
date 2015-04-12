package com.nikolaychernov.activation.backend;

/**
 * Created by Nikolay on 10.04.2015.
 */
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Index

public class User {
    @Id Long id;
    String email;
    String token;
    int access;


    private User() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public User(String email, String token, int access) {
        this.email = email;
        this.token = token;
        this.access = access;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }
}