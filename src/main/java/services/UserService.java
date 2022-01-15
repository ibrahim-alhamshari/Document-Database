package services;

import com.google.gson.Gson;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class UserService {  // singleton pattern

    private static UserService instance = new UserService();
    private static List<User> userList = new ArrayList<>();

    private UserService(){}


    public static UserService getInstance(){
        return instance;
    }



    public List<User> getAllUsers() throws IOException {

        userList = new ArrayList<>();
       FileReader fileReader = new FileReader("src/main/resources/users");

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = null;
        Gson gson = new Gson();

        while ((line=bufferedReader.readLine()) != null){

            User data = gson.fromJson(line , User.class);
            userList.add(gson.fromJson(line , User.class));
        }
        fileReader.close();

        return userList;
    }


    private void resetFile() throws FileNotFoundException {

        File tmpFile = new File("src/main/resources/tmpFile");
        File oldFile = new File("src/main/resources/users");


        Gson gson = new Gson();

        try(FileWriter fileWriter = new FileWriter("src/main/resources/tmpFile" , true)) {

            userList.stream().forEach(item->{

                try {
                    fileWriter.write(gson.toJson(item));
                    fileWriter.write("\n");
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            fileWriter.close();

            if (oldFile.delete()) {
                System.out.println("Deleted the file: " + oldFile.getName());
            } else {
                System.out.println("Failed to delete the file.");
            }

            tmpFile.renameTo(new File("src/main/resources/users"));

            userList = getAllUsers();

        }catch (IOException e){
            e.printStackTrace();

        }

    }



    public boolean isUserFound(String username , String password) throws IOException {
        userList = getAllUsers();
       return userList.stream().anyMatch(e-> (e.getUsername().equalsIgnoreCase(username) && e.getPassword().equals(password)));
    }


    public boolean isAdmin(String username){
        return username.equalsIgnoreCase("admin");
    }



    public User getUserByUserName(String username){
       List<User> _user = userList.stream().filter(e-> e.getUsername().equalsIgnoreCase(username)).collect(Collectors.toList());
        if(_user.size() >0){
            return _user.get(0);
        }
        return null;
    }



    public void createUser(User user){

    }


    public void updateUser(User user){
        userList.stream().forEach(item->{
            if(item.getUsername().equalsIgnoreCase(user.getUsername())){
                item = user;
            }
        });
    }


    public void deleteUser(User user) throws FileNotFoundException {
        userList.remove(user);

        if(userList.size()==0)
            return;

        AtomicLong index= new AtomicLong(1);

        userList.stream().forEach(item->{
            item.setId(index.getAndIncrement());
        });

        Gson gson = new Gson();

        resetFile();
    }

}
