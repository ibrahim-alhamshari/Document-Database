package services;

import com.google.gson.Gson;
import model.Task;
import model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ServicesLayer {  // singleton pattern

    private static ServicesLayer instance = new ServicesLayer();
    private static List<User> userList = new ArrayList<>();
    private static List<Task> taskList = new ArrayList<>();

    private ServicesLayer() {
        System.out.println("An instance has created ...");
    }


    public static ServicesLayer getInstance() {
        return instance;
    }


    public List<User> getAllUsers() {

        userList = new ArrayList<>();
        //FileReader read the data one char by one, so I don't need it. I need to read line by line.
        try (FileReader fileReader = new FileReader("src/main/resources/users")) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            Gson gson = new Gson();

            while ((line = bufferedReader.readLine()) != null) {

                User data = gson.fromJson(line, User.class);
                userList.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }


    public List<Task> getAllTasks(){
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

        return taskList;
    }


    public List<Task> getUserTasks(String username) {
        if(Objects.isNull(taskList) || taskList.size()==0){
            taskList =getAllTasks();
        }

        AtomicReference<List<Task>> userTasks = new AtomicReference<>();

        userList.forEach(user->{
            if(user.getUsername().equalsIgnoreCase(username)){
                userTasks.set(user.getTasks());
            }
        });

        return userTasks.get();
    }


    private void resetUsersSchema() {

        File tmpFile = new File("src/main/resources/tmpUserFile");
        File oldFile = new File("src/main/resources/users");
        Gson gson = new Gson();

        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/tmpUserFile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            userList.forEach(item -> {
                try {
                    bufferedWriter.write(gson.toJson(item));
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
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

        try (FileWriter fileWriter = new FileWriter("src/main/resources/tmpTaskFile")){
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            taskList.forEach(task -> {
                try {
                    bufferedWriter.write(gson.toJson(task));
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
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


    public boolean isAdmin(User user) {
        return user.getRole().equals(User.Role.ADMIN);
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
        if(newUser.getPassword()==null){
            newUser.setPassword("admin");
        }
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
