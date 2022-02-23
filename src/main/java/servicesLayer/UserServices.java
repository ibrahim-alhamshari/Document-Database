package servicesLayer;

import com.google.gson.Gson;
import model.User;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class UserServices {

    private static volatile UserServices instance;
//    private static List<User> userList;
    private static HashMap<String , User> hashMap;
    private static Lock lock = new ReentrantLock(true);
    private static long index; //The index for the new user.
    private static HashMap<String, User> cacheHashMap; //It represents the cache in my application


    private UserServices() {
        instance = null;
        hashMap = getAllDataFromDB();
        index = initializeIndex();
        cacheHashMap = new HashMap<>();
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

        if(hashMap.isEmpty()){

            index = 1; //no objects in the DB.

        }else {
            //Get the index of last object in the DB and increment it by 1.
            index = 1;
            hashMap.entrySet().forEach(e ->{
                if(e.getValue().getId()>index){
                    index = e.getValue().getId() + 1;
                }
            });
        }

        return index;
    }


    private static HashMap<String , User> getAllDataFromDB() {

        lock.lock();

        try (FileReader fileReader = new FileReader("src/main/resources/database/users");
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            //Create a string to hold the JSON string in each line.
            String line = null;
            Gson gson = new Gson();

            hashMap = new HashMap<>();

            //loop through the lines while there are a lines in the DB.
            while ((line = bufferedReader.readLine()) != null) {

                //Convert the JSON string to a JSON object and add them to the list.
                User data = gson.fromJson(line, User.class);
                hashMap.put(data.getUsername() , data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return hashMap;
    }


    private void saveChangesToDB() {

        lock.lock();

        try {
            File tmpFile = new File("src/main/resources/database/tmpUserFile");
            File oldFile = new File("src/main/resources/database/users");
            Gson gson = new Gson();

            FileWriter fileWriter = new FileWriter("src/main/resources/database/tmpUserFile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            hashMap.entrySet().forEach(user -> {
                try {
                    //Converting user object to JSON string format
                    String jsonData = gson.toJson(user.getValue());

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

            hashMap = getAllDataFromDB();
            cacheHashMap = new HashMap<>();// When the DB updated, we have to update the cache.

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }


    public HashMap<String , User> getDataFromHashMap(){
        return hashMap;
    }


    public boolean isUserFound(String username, String password) throws IOException {
        if(hashMap.containsKey(username) && hashMap.get(username).getPassword().equals(password)){
            return true;
        }
        return false;
    }


    public boolean isAdmin(User user) {
        return user.getRole().equals(User.Role.ADMIN);
    }


    public User getUserByUserName(String username) {

        User userFromCache = cacheHashMap.get(username); //Get object from cache

        if(Objects.nonNull(userFromCache)){
            return userFromCache;
        }


        User _user = hashMap.get(username);
        if (Objects.nonNull(_user)) {
            cacheHashMap.put(username , _user); // Add this object to the cache.
            return _user; // The username is unique, so it will be one object only in the list.
        }

        return new User();
    }



    public User createUser(User newUser) throws IOException {

        if(Objects.isNull(newUser.getRole()) || newUser.getRole().equals(User.Role.ADMIN)){
            newUser.setPassword("admin");
        }

        newUser.setId(index);

        hashMap.put(newUser.getUsername() , newUser);

        saveChangesToDB();

        index++;

        return newUser;
    }



    public User updateUser(User user) throws FileNotFoundException {

        hashMap.put(user.getUsername(), user);
        saveChangesToDB();

        user = null;

        return user;
    }


    public void removeUser(User user) throws FileNotFoundException {

        hashMap.remove(user.getUsername());
        saveChangesToDB();
    }

}
