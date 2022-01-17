package services;

import com.google.gson.Gson;
import model.Task;
import model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class UserService {  // singleton pattern

    private static UserService instance = new UserService();
    private static List<User> userList = new ArrayList<>();
    private static List<Task> taskList = new ArrayList<>();

    private UserService() {
    }


    public static UserService getInstance() {
        return instance;
    }


    public List<User> getAllUsers() {

        userList = new ArrayList<>();
        try (FileReader fileReader = new FileReader("src/main/resources/users")) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            Gson gson = new Gson();

            while ((line = bufferedReader.readLine()) != null) {

                User data = gson.fromJson(line, User.class);
                userList.add(gson.fromJson(line, User.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }


    public List<Task> getUserTasks(String username) throws FileNotFoundException {

        List<Task> userTasks = new ArrayList<>();
        taskList = new ArrayList<>();

        try (FileReader fileReader = new FileReader("src/main/resources/tasks")) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line =null;
            Gson gson = new Gson();

            while((line = bufferedReader.readLine()) != null){
                Task task = gson.fromJson(line , Task.class);
                taskList.add(task);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        taskList.forEach(task->{
            if(task.getUser().getUsername().equalsIgnoreCase(username)){
                userTasks.add(task);
            }
        });

        return userTasks;
    }


    private void resetUsersSchema() throws FileNotFoundException {

        File tmpFile = new File("src/main/resources/tmpUserFile");
        File oldFile = new File("src/main/resources/users");
        Gson gson = new Gson();

        try (FileWriter fileWriter = new FileWriter("src/main/resources/tmpUserFile", true)) {

            userList.forEach(item -> {
                try {
                    fileWriter.write(gson.toJson(item));
                    fileWriter.write("\n");
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            fileWriter.close();
            oldFile.delete();
            tmpFile.renameTo(new File("src/main/resources/users"));

            userList = getAllUsers();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    private void resetTaskSchema() {
        File tmpFile = new File("src/main/resources/tmpTaskFile");
        File oldFile = new File("src/main/resources/tasks");
        Gson gson = new Gson();

        try (FileWriter fileWriter = new FileWriter("src/main/resources/tmpTaskFile")) {
            taskList.forEach(task -> {
                try {
                    fileWriter.write(gson.toJson(task));
                    fileWriter.write("\n");
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        oldFile.delete();
        tmpFile.renameTo(new File("src/main/resources/tasks"));
    }


    public boolean isUserFound(String username, String password) throws IOException {
        userList = getAllUsers();
        return userList.stream().anyMatch(e -> (e.getUsername().equalsIgnoreCase(username) && e.getPassword().equals(password)));
    }


    public boolean isAdmin(String username) {
        return username.equalsIgnoreCase("admin");
    }


    public User getUserByUserName(String username) {
        List<User> _user = userList.stream().filter(e -> e.getUsername().equalsIgnoreCase(username)).collect(Collectors.toList());
        if (_user.size() > 0) {
            return _user.get(0);
        }
        return null;
    }


    public void createUser(User newUser) throws IOException {

        long lines = 0;
        lines = Files.lines(Paths.get("src/main/resources/users")).count() + 1;
        newUser.setId(lines);
        userList.add(newUser);
        resetUsersSchema();

    }


    public void createTask(Task newTask) throws IOException {
        long lines = 0;
        lines = Files.lines(Paths.get("src/main/resources/tasks")).count() + 1;
        newTask.setId(lines);
        taskList.add(newTask);
        resetTaskSchema();
    }


    public void updateUser(User user) throws FileNotFoundException {

        userList.stream().forEach(item -> {
            if (item.getUsername().equalsIgnoreCase(user.getUsername())) {
                item = user;
            }
        });

        resetUsersSchema();
    }


    public void removeUser(User user) throws FileNotFoundException {
        userList.remove(user);

        if (userList.size() == 0)
            return;

        AtomicLong index = new AtomicLong(1);

        userList.stream().forEach(item -> {
            item.setId(index.getAndIncrement());
        });

        resetUsersSchema();
    }

}
