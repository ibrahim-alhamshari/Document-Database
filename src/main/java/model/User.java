package model;

import sun.security.util.Password;

import java.time.LocalDateTime;

public class User {

    private long id;
    private LocalDateTime registerDate;
    private String username;
    private String password;
    private String email;

    public User() {
    }

    public User(long id, LocalDateTime registerDate, String username, String password, String email) {
        this.id = id;
        this.registerDate = registerDate;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\"=\"" + id +
                "\", \"registerDate\"=\"" + registerDate +
                "\", \"username\"=\"" + username  +
                "\", \"password\"=\"" + password +
                "\", \"email\"=\"" + email +
                "\"}";
    }
}
