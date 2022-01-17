package databaseClasses;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import services.UserService;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class App implements Runnable{


    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(){
            public void run(){
                UserService userService1 = UserService.getInstance();
                System.out.println(userService1);
            }
        };

        Thread thread2 = new Thread(){
            public void run(){
                UserService userService2 = UserService.getInstance();
                System.out.println(userService2);
            }
        };
        thread.start();
        thread2.start();

    }




    public static void main2(String[] args) throws IOException {
//        File file = new File("unicorns");

//        BufferedReader reader = new BufferedReader(new FileReader("unicorns"));
//        Scanner scanner = new Scanner(reader);
//
//        while (scanner.hasNext())
//            System.out.println(scanner.next());

//        Path path = Paths.get("unicorns");
//        try (BufferedReader reader = Files.newBufferedReader(path)){ //Try with resource
//            // Try will handle closing this resource after being done with that resource.
//
//            String line = reader.readLine();
//
//            while (line !=null){
//                System.out.println(line);
//                line = reader.readLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // ************************************************************************************************

//        Contact contact = new Contact("Ibrahim" , "Alhamshari" , 25);


       User user = new User(LocalDateTime.now() , "Admin" , "admin" , "admin@gmail.com");
       Gson gson = new Gson();
       String data = gson.toJson(user);



        try (FileWriter fileWriter = new FileWriter("src/main/resources/recentquotes" , true)){
            fileWriter.write(data);
            fileWriter.write("\n");
            fileWriter.flush();
        }catch (IOException e){
            e.printStackTrace();
        }

        Path path = Paths.get("src/main/resources/recentquotes");

        long lines=0;
        lines = Files.lines(path).count();
        System.out.println(lines);


////        Contact contactList = gson.fromJson(contactJson , Contact.class);
//        Contact contact1 =  gson.fromJson(contactJson , Contact.class);
//        System.out.println(contact1);
//        List<Contact> contactList = gson.fromJson(contactJson , List.class);
//        System.out.println(contactList);
//        Type type = new TypeToken<List<Contact>>(){}.getType();
//        List<Contact> contactList = gson.fromJson(contactJson , type);
//
//        for (Contact contact: contactList){
//            System.out.println(contact);
//        }


//        FileReader reader = new FileReader("src/main/resources/addingFromClient");
////        Gson gson = new Gson();
////
//        List<Map> mapList = gson.fromJson(reader , List.class);
//        int min=0;
//        int max= mapList.size()-1;
//        int j= (int) (Math.random()*(max-min+1)+min);
//
//        Map map = mapList.get(j);
//
//        System.out.println(map);

        //*****************************************************************************************************************

//        FileOutputStream outputStream = new FileOutputStream("src/main/resources/addingFromClient" , true);
//////        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, Arrays.toString("\"Hello\":\"world\"".getBytes())));
//        outputStream.write( jsonObject.toJSONString().getBytes());
//        outputStream.flush();
//        outputStream.close();



    }

    @Override
    public void run() {

    }
}
