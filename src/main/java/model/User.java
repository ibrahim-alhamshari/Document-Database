package model;

import sun.security.util.Password;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class User {

    private Long id;
    private final LocalDateTime registerDate;
    private final String username;
    private final Role role;
    private String password;
    private String email;
    private Address address;
    private List<Task> tasks;

    public static class Builder{
        //Required parameter
        private final String username;

        private String password =null;
        private Long id=null;
        private String email = null;
        private Role role= null;
        private List<Task> tasks= new ArrayList<>();
        private Address address=null;

        public Builder(String username){
            this.username =username;
        }

        public Builder password(String value){
            password = value;
            return this;
        }

        public Builder id(Long value){
            id =value;
            return this;
        }

        public Builder email(String value){
            email =value;
            return this;
        }

        public Builder tasks(List<Task> value){
            tasks=value;
            return this;
        }

        public Builder role(Role value){
            role = value;
            return this;
        }

        public Builder address(Address value){
            address =value;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }

    private User(Builder builder){
        this.registerDate =LocalDateTime.now();
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.tasks= builder.tasks;
        this.role=builder.role;
        this.id= builder.id;
        this.address= builder.address;
    }


    public enum Role{
        ADMIN,
        USER
    }



    public Long getId() {
        return id;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Role getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(Address address) {
        this.address = address;
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
