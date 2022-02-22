package servicesLayer;

import com.google.gson.Gson;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public final class UserServices {

    private static volatile UserServices instance;
    private static List<User> userList;
    private static Lock lock = new ReentrantLock(true);
    private static long index; //The index for the new user.
    private static Hashtable<String, User> hashtable; //It represents the cache in my application


    private UserServices() {
        instance = null;
        userList = getAllDataFromDB();
        index = initializeIndex();
        hashtable = new Hashtable<>();
    }


    public static UserServices getInstance() {

        if(instance==null){
            lock.lock();//Acquire the lock
                if(instance==null){
                    instance = new UserServices();
                }
            lock.unlock();//Release the lock
        }

        return instance;
    }



    private long initializeIndex(){

        if(userList.size()>0){

            //Get the index of last object in the DB and increment it by 1.
            index = userList.get(userList.size()-1).getId() +1;

        }else {
            index=1; //no objects in the DB.
        }

        return index;
    }


    private static List<User> getAllDataFromDB() {
        lock.lock();

        try (FileReader fileReader = new FileReader("src/main/resources/database/users");
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            //Create a string to hold the JSON string in each line.
            String line = null;
            Gson gson = new Gson();

            userList = new ArrayList<>();

            //loop through the lines while there are a lines in the DB.
            while ((line = bufferedReader.readLine()) != null) {

                //Convert the JSON string to a JSON object and add them to the list.
                User data = gson.fromJson(line, User.class);
                userList.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return userList;
    }


    private void saveChangesToDB() {

        lock.lock();

        try {
            File tmpFile = new File("src/main/resources/database/tmpUserFile");
            File oldFile = new File("src/main/resources/database/users");
            Gson gson = new Gson();

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

                } catch (IOException |OutOfMemoryError e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
            fileWriter.close();
            oldFile.delete();
            tmpFile.renameTo(new File("src/main/resources/database/users"));

            userList = getAllDataFromDB();
            hashtable = new Hashtable<>();// When the DB updated, we have to update the cache.

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }



    public List<User> getDataFromUserList(){
        return userList;
    }


    public boolean isUserFound(String username, String password) throws IOException {
        return userList.stream().anyMatch(e -> (e.getUsername().equalsIgnoreCase(username) && e.getPassword().equals(password)));
    }


    public boolean isAdmin(User user) {
        return user.getRole().equals(User.Role.ADMIN);
    }


    public User getUserByUserName(String username) {

        User userFromCache = hashtable.get(username); //Get object from cache

        if(Objects.nonNull(userFromCache)){
            return userFromCache;
        }


        List<User> _user = userList.stream().filter(e -> e.getUsername().equalsIgnoreCase(username)).collect(Collectors.toList());
        if (_user.size() > 0) {
            hashtable.put(username , _user.get(0)); // Add this object to the cache.
            return _user.get(0); // The username is unique, so it will be one object only in the list.
        }

        return new User();
    }



    public User createUser(User newUser) throws IOException {

        newUser.setId(index);

        if(Objects.isNull(newUser.getRole()) || newUser.getRole().equals(User.Role.ADMIN)){
            newUser.setPassword("admin");
        }

        userList.add(newUser);
        saveChangesToDB();
        index++;

        return newUser;
    }



    public User updateUser(User user) throws FileNotFoundException {

        for (User object: userList) {

            if (object.getUsername().equalsIgnoreCase(user.getUsername())) {
                object = user;
                break;
            }

        }
        saveChangesToDB();
        user =null;

        return user;
    }


    public void removeUser(User user) throws FileNotFoundException {

        userList.remove(user);
        saveChangesToDB();
    }

}
