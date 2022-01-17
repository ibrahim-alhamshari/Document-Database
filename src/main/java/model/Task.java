package model;

public class Task {

    private long id;
    private String subject;
    private String description;
    private User user;

    public Task(){}

    public Task(String subject , String description , User user){
        this.subject = subject;
        this.description =description;
        this.user=user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", user=" + user +
                '}';
    }
}
