package servicesLayer;

import com.google.gson.Gson;
import model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class UserServices {  // singleton pattern

    private static volatile UserServices instance = null;
    private static List<User> userList = getAllUsers();

    private UserServices() {
        System.out.println("An instance of \"ServicesLayer\" has created ...");
    }


    public static UserServices getInstance() {

        if(instance==null){
            synchronized (UserServices.class){
                if(instance==null){
                    instance = new UserServices();
                }
            }
        }

        return instance;
    }


    public static List<User> getAllUsers() {

        userList = new ArrayList<>();

        //FileReader read the data one char by one, so I don't need it. I need to read line by line.
        try (FileReader fileReader = new FileReader("src/main/resources/database/users")) {

            //Initialize bufferedReader
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            //Create a string to hold the JSON string in each line.
            String line = null;
            Gson gson = new Gson();

            //loop through the lines while there are a lines in the DB.
            while ((line = bufferedReader.readLine()) != null) {

                //Convert the JSON string to a JSON object and add them to the list.
                User data = gson.fromJson(line, User.class);
                userList.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }



    private void saveUserChangesToDB() {

        File tmpFile = new File("src/main/resources/database/tmpUserFile");
        File oldFile = new File("src/main/resources/database/users");
        Gson gson = new Gson();

        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/database/tmpUserFile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            userList.forEach(user -> {
                try {

                    //Converting user object to JSON string format
                    String jsonData = gson.toJson(user);

                    //Writing the JSON string to the text file
                    bufferedWriter.write(jsonData);

                    //used to separate the next line as a new line.
                    bufferedWriter.newLine();

                    //flush the data from buffedWriter
                    bufferedWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
            fileWriter.close();
            oldFile.delete();
            tmpFile.renameTo(new File("src/main/resources/database/users"));

            userList = getAllUsers();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }



    public boolean isUserFound(String username, String password) throws IOException {
        userList = getAllUsers();
        return userList.stream().anyMatch(e -> (e.getUsername().equalsIgnoreCase(username) && e.getPassword().equals(password)));
    }


    public boolean isAdmin(User user) {
        return user.getRole().equals(User.Role.ADMIN);
    }


    public User getUserByUserName(String username) {
        List<User> _user = userList.stream().filter(e -> e.getUsername().equalsIgnoreCase(username)).collect(Collectors.toList());
        if (_user.size() > 0) {
            return _user.get(0);
        }
        return new User();
    }


    public User createUser(User newUser) throws IOException {

        long lines = 0;
        lines = Files.lines(Paths.get("src/main/resources/database/users")).count() + 1;
        newUser.setId(lines);
        if(newUser.getPassword()==null){
            newUser.setPassword("admin");
        }
        userList.add(newUser);
        saveUserChangesToDB();

        return newUser;
    }



    public User updateUser(User user) throws FileNotFoundException {

        userList.stream().forEach(item -> {
            if (item.getUsername().equalsIgnoreCase(user.getUsername())) {
                item = user;
            }
        });

        saveUserChangesToDB();

        return user;
    }


    public void removeUser(User user) throws FileNotFoundException {
        userList.remove(user);

        if (userList.size() == 0)
            return;

        AtomicLong index = new AtomicLong(1);

        userList.stream().forEach(item ->item.setId(index.getAndIncrement()));

        saveUserChangesToDB();
    }

}
