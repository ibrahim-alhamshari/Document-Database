import model.Task;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import servicesLayer.UserServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

public class UserServicesTest {

    @Test
    @DisplayName("Test for getUserByUserName() method")
    public void getUserByUserName() throws IOException {

        UserServices userServices = UserServices.getInstance();

        User user = new User("loay" , "123456789" ,"loay@gmail.com" );

        userServices.createUser(user);

        assertEquals("loay" , userServices.getUserByUserName("loay").getUsername());
        assertEquals("123456789" , userServices.getUserByUserName("loay").getPassword());
        assertEquals("loay@gmail.com" , userServices.getUserByUserName("loay").getEmail());
    }


    @Test
    @DisplayName("Test for isUserFound() method")
    public void isUserFound() throws IOException {

        UserServices userServices = UserServices.getInstance();

//        userServices.createUser(new User("loay" , "123456789" ,"loay@gmail.com" ));
//        userServices.createUser(new User("Waleed" , "12345678" ,"waleed@yahoo.com" ));

        assertTrue(userServices.isUserFound("loay" , "123456789"));
        assertFalse(userServices.isUserFound("loay" , "123456"));
        assertTrue(userServices.isUserFound("Ahmad" , "12345678"));
        assertFalse(userServices.isUserFound("Ahmad", "123456789"));
    }


    @Test
    @DisplayName("Test for isAdmin() method")
    public void isAdmin() throws IOException {
        UserServices userServices = UserServices.getInstance();

        User user1 = userServices.getUserByUserName("loay");
        User user2 = userServices.getUserByUserName("Ahmad");

        user1.setRole(User.Role.ADMIN);
        user2.setRole(User.Role.USER);

        userServices.createUser(user1);
        userServices.createUser(user2);

        assertTrue(userServices.isAdmin(user1));
        assertFalse(userServices.isAdmin(user2));
    }


    @Test
    @DisplayName("Test for getAllUsers() method")
    public void getAllUsers() throws IOException {
        List<User> userList = new ArrayList<>();
//        userList.addAll(servicesLayer.UserServices.getInstance().getDataFromUserList());

        Assertions.assertEquals(userList.toString(), servicesLayer.UserServices.getInstance().getDataFromHashMap().toString());
    }



    @Test
    @DisplayName("Test for getUserTasks() method")
    public void getUserTasks() throws IOException {
        UserServices userServices = UserServices.getInstance();

        Task task1 = new Task("Reading" , "I have some reading");
        Task task2 = new Task("Writing" , "I have some writing");

        List<Task> taskList =new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);
        User user = userServices.getUserByUserName("loay");

        user.setTasks(taskList);
        userServices.updateUser(user);

        assertEquals(taskList.toString() , user.getTasks().toString());
    }

}
