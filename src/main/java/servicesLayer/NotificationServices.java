package servicesLayer;

import com.google.gson.Gson;
import model.Notification;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public final class NotificationServices {

    private static volatile NotificationServices instance;
    private static List<Notification> cacheList;
    private static Lock lock= new ReentrantLock(true);
    private static long index; //The index for the new notifications


    private NotificationServices(){
        instance = null;
        cacheList = getAllDataFromDB();
        index = initializeIndex();
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

        if(cacheList.size()>0){

            //Get the index of last object in the DB and increment it by 1.
            index = cacheList.get(cacheList.size()-1).getId() +1;

        }else {
            index=1;//no objects in the DB.
        }

        return index;
    }


    private static List<Notification> getAllDataFromDB(){

        try (FileReader fileReader = new FileReader("src/main/resources/database/notifications")){
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            Gson gson = new Gson();
            cacheList = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                Notification data = gson.fromJson(line, Notification.class);
                cacheList.add(data);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return cacheList;
    }


    private void saveChangesToDB(){

        try {
            lock.lock();

            File tmpFile = new File("src/main/resources/database/tmpNotificationFile");
            File oldFile = new File("src/main/resources/database/notifications");
            Gson gson = new Gson();

            FileWriter fileWriter = new FileWriter("src/main/resources/database/tmpNotificationFile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            cacheList.forEach(user -> {
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

            cacheList = getAllDataFromDB(); // When the DB updated, we have to update the cache.

        } catch (IOException e) {
            e.printStackTrace();

        }

        lock.unlock();

    }


    public List<Notification> getDataFromCache(){
        return cacheList;
    }


    public Notification createNotification(Notification notification) throws IOException{

        notification.setId(index);
        cacheList.add(notification);
        saveChangesToDB();
        index++;

        return notification;

    }


}
