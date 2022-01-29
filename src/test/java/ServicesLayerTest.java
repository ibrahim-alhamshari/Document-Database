import model.Task;
import model.User;
import org.junit.jupiter.api.Test;
import servicesLayer.UserServices;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

public class ServicesLayerTest {

    @Test
    public void getUserByUserName() throws IOException {
        UserServices servicesLayer = UserServices.getInstance();
        User user = new User.Builder("loay").password("#iva45#@*&").email("loay@gmail.com").build();

        servicesLayer.createUser(user);

        assertEquals("loay" , servicesLayer.getUserByUserName("loay").getUsername());
        assertEquals("#iva45#@*&" , servicesLayer.getUserByUserName("loay").getPassword());
        assertEquals("loay@gmail.com" , servicesLayer.getUserByUserName("loay").getEmail());
    }

    @Test
    public void isUserFound() throws IOException {
        UserServices servicesLayer = UserServices.getInstance();


        servicesLayer.createUser(new User.Builder("loay").password("#iva45#@*&").email("loay@gmail.com").build());
        servicesLayer.createUser(new User.Builder("Waleed").password("jhw8#@5!lw").email("waleed@yahoo.com").build());

        assertTrue(servicesLayer.isUserFound("loay" , "#iva45#@*&"));
        assertFalse(servicesLayer.isUserFound("Ahmad", "565s3zkx"));
        assertTrue(servicesLayer.isUserFound("Waleed", "jhw8#@5!lw"));
        assertFalse(servicesLayer.isUserFound("loay" , "jt7!&8js"));
    }

    @Test
    public void isAdmin() throws IOException {
        UserServices servicesLayer = UserServices.getInstance();

        User user1 = servicesLayer.createUser(new User.Builder("loay").password("#iva45#@*&").email("loay@gmail.com").build());
        User user2 = servicesLayer.createUser(new User.Builder("Ahmad").password("123445").email("ahmad@gmail.com").build());
        User user3 = servicesLayer.createUser(new User.Builder("Rami").password("rherve65f").email("rami@gmail.com").build());

//        user1.setRole(User.Role.ADMIN);
//        user2.setRole(User.Role.USER);
//        user3.setRole(User.Role.USER);

        servicesLayer.createUser(user1);
        servicesLayer.createUser(user2);
        servicesLayer.createUser(user3);

        assertTrue(servicesLayer.isAdmin(user1));
        assertFalse(servicesLayer.isAdmin(user2));
        assertFalse(servicesLayer.isAdmin(user3));

    }


    @Test
    public void getAllUsers() throws IOException {
        List<User> userList = new ArrayList<>();
        userList.addAll(UserServices.getAllUsers());

        assertEquals(userList.toString(), UserServices.getAllUsers().toString());
    }



    @Test
    public void getUserTasks() throws IOException {
        UserServices servicesLayer= UserServices.getInstance();

        Task task1 = new Task("Reading" , "I have some reading");
        Task task2 = new Task("Writing" , "I have some writing");

        List<Task> taskList =new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);

        User user = servicesLayer.updateUser( new User.Builder("Ahmad").password("1254").email("ahmad@gmail.com").tasks(taskList).build());

        assertEquals(taskList.toString() , user.getTasks().toString());
    }

}
