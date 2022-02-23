package servicesLayer;

import com.google.gson.Gson;
import model.Address;
import model.Notification;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public final class NotificationServices {

    private static volatile NotificationServices instance;
//    private static List<Notification> notificationList;
private static HashMap<String , Notification> hashMap;
    private static Lock lock= new ReentrantLock(true);
    private static long index; //The index for the new notifications
    private static Hashtable<String, Notification> hashtable; //It represents the cache in my application


    private NotificationServices(){
        instance = null;
        hashMap = getAllDataFromDB();
        index = initializeIndex();
        hashtable = new Hashtable<>();
    }


    public static NotificationServices getInstance(){

        if(instance ==null){
            lock.lock();
            if (Objects.isNull(instance)) {
                instance= new NotificationServices();
            }
            lock.unlock();
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


    private static HashMap<String, Notification> getAllDataFromDB(){
        lock.lock();

        try (FileReader fileReader = new FileReader("src/main/resources/database/notifications");
             BufferedReader bufferedReader = new BufferedReader(fileReader)){

            String line = null;
            Gson gson = new Gson();
            hashMap = new HashMap<>();

            while ((line = bufferedReader.readLine()) != null) {
                Notification data = gson.fromJson(line, Notification.class);
                hashMap.put(data.getProcess() , data);
            }

        }catch (IOException e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return hashMap;
    }


    private void saveChangesToDB(){

        lock.lock();

        try {

            File tmpFile = new File("src/main/resources/database/tmpNotificationFile");
            File oldFile = new File("src/main/resources/database/notifications");
            Gson gson = new Gson();

            FileWriter fileWriter = new FileWriter("src/main/resources/database/tmpNotificationFile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            hashMap.entrySet().forEach(item -> {
                try {

                    String jsonData = gson.toJson(item.getValue());
                    bufferedWriter.write(jsonData);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                } catch (IOException | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
            fileWriter.close();
            oldFile.delete();
            tmpFile.renameTo(new File("src/main/resources/database/notifications"));

            hashMap = getAllDataFromDB();
            hashtable = new Hashtable<>();// When the DB updated, we have to update the cache.

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            lock.unlock();
        }

    }


    public HashMap<String , Notification> getDataFromHashMap(){
        return hashMap;
    }


    public Notification createNotification(Notification notification) throws IOException{

        notification.setId(index);

        hashMap.put(notification.getProcess() , notification);

        saveChangesToDB();
        index++;

        return notification;

    }


}
