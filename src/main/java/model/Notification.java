package model;

import java.time.LocalDateTime;

public class Notification {

    private long id;
    private String process;
    private final LocalDateTime notificationDate;
    private User user;

    public Notification(String process, User user) {
        this.process = process;
        this.user = user;
        this.notificationDate= LocalDateTime.now();
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", process='" + process + '\'' +
                ", notificationDate=" + notificationDate +
                ", user=" + user +
                '}';
    }
}
