package model;

import sun.security.util.Password;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {

    private Long id;
    private LocalDateTime registerDate;
    private String username;
    private Role role;
    private String password;
    private String email;
    private Address address;
    private List<Task> tasks;

    public User(){
    }

    public User(String username){
        this(username , null);
    }

    public User(String username , String password){
        this(username , password , null);
    }

    public User(String username , String password , String email){
        this(username , password , email , null);
    }

    public User(String username , String password , String email , Role role){
        this.username = username;
        this.password =password;
        this.email= email;
        this.role =role;
        this.registerDate =LocalDateTime.now();
    }


    public enum Role{
        ADMIN,
        USER
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", registerDate=" + registerDate +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", address=" + address +
                ", tasks=" + tasks +
                ", role=" + role +
                '}';
    }
}
