package model;

import java.time.LocalDateTime;

public class Notification {

    private String subject;
    private final LocalDateTime notificationDate;
    private User user;

    public Notification(String subject, User user) {
        this.subject = subject;
        this.user = user;
        this.notificationDate= LocalDateTime.now();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
                "subject='" + subject + '\'' +
                ", notificationDate=" + notificationDate +
                ", user=" + user +
                '}';
    }
}
