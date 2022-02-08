import model.Notification;
import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import servicesLayer.NotificationServices;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class NotificationServicesTest {

    @Test
    @DisplayName("Test for createNotification() method")
    public void createNotification() throws IOException {
        User user = new User("Ahmad" , "12345");
        Notification notification = new Notification( user , "Delete" );

        NotificationServices notificationServices = NotificationServices.getInstance();

        assertEquals(notification ,notificationServices.createNotification(notification) );
    }


    @Test
    @DisplayName("Test for getAllNotifications() method")
    public void getAllNotifications(){

        NotificationServices notificationServices = NotificationServices.getInstance();

        assertEquals(3, notificationServices.getDataFromCache().size());
    }
}
