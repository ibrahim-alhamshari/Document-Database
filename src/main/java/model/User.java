package model;

import sun.security.util.Password;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {

    private long id;
    private LocalDateTime registerDate;
    private String username;
    private String password;
    private String email;
    private List<Task> tasks;
    private Role role;

    public User() {
    }

    public User(LocalDateTime registerDate, String username, String password, String email ) {
        this.registerDate = registerDate;
        this.username = username;
        this.password = password;
        this.email = email;
        tasks= new ArrayList<>();
    }


    public enum Role{
        ADMIN,
        USER
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", registerDate=" + getRegisterDate() +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", tasks=" + getTasks() +
                ", role=" + getRole() +
                '}';
    }


    //    @Override
//    public String toString() {
//        return "{" +
//                "\"id\"=\"" + id +
//                "\", \"registerDate\"=\"" + registerDate +
//                "\", \"username\"=\"" + username  +
//                "\", \"password\"=\"" + password +
//                "\", \"email\"=\"" + email +
//                "\"}";
//    }
}
