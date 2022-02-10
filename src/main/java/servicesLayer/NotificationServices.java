package servicesLayer;

import com.google.gson.Gson;
import model.Address;
import model.Notification;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public final class NotificationServices {

    private static volatile NotificationServices instance;
    private static List<Notification> notificationList;
    private static Lock lock= new ReentrantLock(true);
    private static long index; //The index for the new notifications
    private static Hashtable<String, Notification> hashtable; //It represents the cache in my application


    private NotificationServices(){
        instance = null;
        notificationList = getAllDataFromDB();
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

        if(notificationList.size()>0){

            //Get the index of last object in the DB and increment it by 1.
            index = notificationList.get(notificationList.size()-1).getId() +1;

        }else {
            index=1;//no objects in the DB.
        }

        return index;
    }


    private static List<Notification> getAllDataFromDB(){
        lock.lock();

        try (FileReader fileReader = new FileReader("src/main/resources/database/notifications")){
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            Gson gson = new Gson();
            notificationList = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                Notification data = gson.fromJson(line, Notification.class);
                notificationList.add(data);
            }

        }catch (IOException e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return notificationList;
    }


    private void saveChangesToDB(){

        lock.lock();

        try {

            File tmpFile = new File("src/main/resources/database/tmpNotificationFile");
            File oldFile = new File("src/main/resources/database/notifications");
            Gson gson = new Gson();

            FileWriter fileWriter = new FileWriter("src/main/resources/database/tmpNotificationFile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            notificationList.forEach(user -> {
                try {

                    String jsonData = gson.toJson(user);
                    bufferedWriter.write(jsonData);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
            fileWriter.close();
            oldFile.delete();
            tmpFile.renameTo(new File("src/main/resources/database/notifications"));

            notificationList = getAllDataFromDB();
            hashtable = new Hashtable<>();// When the DB updated, we have to update the cache.

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            lock.unlock();
        }

    }


    public List<Notification> getDataFromNotificationList(){
        return notificationList;
    }


    public Notification createNotification(Notification notification) throws IOException{

        notification.setId(index);
        notificationList.add(notification);
        saveChangesToDB();
        index++;

        return notification;

    }


}
