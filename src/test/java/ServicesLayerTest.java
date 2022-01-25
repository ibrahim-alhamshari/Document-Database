import model.Task;
import model.User;
import org.junit.jupiter.api.Test;
import services.ServicesLayer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.sun.javaws.JnlpxArgs.verify;
//import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

public class ServicesLayerTest {

    @Test
    public void getUserByUserName() throws IOException {
        ServicesLayer servicesLayer = ServicesLayer.getInstance();

        servicesLayer.createUser(new User(LocalDateTime.now() , "loay" , "#iva45#@*&" , "loay@gmail.com"));

        assertEquals("loay" , servicesLayer.getUserByUserName("loay").getUsername());
        assertEquals("#iva45#@*&" , servicesLayer.getUserByUserName("loay").getPassword());
        assertEquals("loay@gmail.com" , servicesLayer.getUserByUserName("loay").getEmail());
    }

    @Test
    public void isUserFound() throws IOException {
        ServicesLayer servicesLayer = ServicesLayer.getInstance();
        servicesLayer.createUser(new User(LocalDateTime.now() , "loay" , "#iva45#@*&" , "loay@gmail.com"));
        servicesLayer.createUser(new User(LocalDateTime.now() , "Waleed" , "jhw8#@5!lw" , "waleed@yahoo.com"));

        assertTrue(servicesLayer.isUserFound("loay" , "#iva45#@*&"));
        assertFalse(servicesLayer.isUserFound("Ahmad", "565s3zkx"));
        assertTrue(servicesLayer.isUserFound("Waleed", "jhw8#@5!lw"));
        assertFalse(servicesLayer.isUserFound("loay" , "jt7!&8js"));
    }

    @Test
    public void isAdmin() throws IOException {
        ServicesLayer servicesLayer = ServicesLayer.getInstance();
        User user1 = new User(LocalDateTime.now() , "loay" , "#iva45#@*&" , "loay@gmail.com");
        User user2 = new User(LocalDateTime.now() , "Ahmad" , "123445" , "ahmad@gmail.com");
        User user3 = new User(LocalDateTime.now() , "Rami" , "rherve65f" , "rami@gmail.com");
        user1.setRole(User.Role.ADMIN);
        user2.setRole(User.Role.USER);
        user3.setRole(User.Role.USER);

        servicesLayer.createUser(user1);
        servicesLayer.createUser(user2);
        servicesLayer.createUser(user3);

        assertTrue(servicesLayer.isAdmin(user1));
        assertFalse(servicesLayer.isAdmin(user2));
        assertFalse(servicesLayer.isAdmin(user3));

    }


    @Test
    public void getAllUsers() throws IOException {
        ServicesLayer servicesLayer=ServicesLayer.getInstance();

        User user1 = new User(LocalDateTime.now() , "loay" , "#iva45#@*&" , "loay@gmail.com");
        User user2 = new User(LocalDateTime.now() , "Ahmad" , "123445" , "ahmad@gmail.com");
        User user3 = new User(LocalDateTime.now() , "Rami" , "rherve65f" , "rami@gmail.com");
        servicesLayer.createUser(user1);
        servicesLayer.createUser(user2);
        servicesLayer.createUser(user3);
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        assertEquals(userList.toString(), servicesLayer.getAllUsers().toString());
    }


    @Test
    public void getAllTasks() throws IOException {
        ServicesLayer servicesLayer = ServicesLayer.getInstance();
        Task task1 = new Task("Reading" , "I have some reading");
        Task task2 = new Task("Writing" , "I have some writing");
        Task task3 = new Task("washing" , "I want to wash the car");
        servicesLayer.createTask(task1);
        servicesLayer.createTask(task2);
        servicesLayer.createTask(task3);

        List<Task> taskList =new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);

        assertEquals(taskList.toString() , servicesLayer.getAllTasks().toString());
    }

    @Test
    public void getUserTasks() throws IOException {
        ServicesLayer servicesLayer= ServicesLayer.getInstance();
        User user = new User(LocalDateTime.now() , "Ahmad" , "1254" , "ahmad@gmail.com");

        Task task1 = new Task("Reading" , "I have some reading");
        Task task2 = new Task("Writing" , "I have some writing");
        List<Task> taskList =new ArrayList<>();

        taskList.add(task1);
        taskList.add(task2);

        user.setTasks(taskList);
        servicesLayer.createUser(user);

        assertEquals(taskList.toString() , servicesLayer.getUserTasks("Ahmad").toString());
    }

}
