package databaseClasses;

import java.io.*;
import java.util.Arrays;

public class App {

    public static void main(String[] args) throws IOException {
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

//        Gson gson= new Gson();
//        String contactObject = gson.toJson(contact);
//        System.out.println( "toJson: " + contactObject);

//        String contactJson = "[{\"firstName\":\"Ibrahim\",\"lastName\":\"Alhamshari\",\"age\":25}," +
//                "{\"firstName\":\"Ali\",\"lastName\":\"Omar\",\"age\":47}," +
//                "{\"firstName\":\"Saed\",\"lastName\":\"Rami\",\"age\":16}]";  //if one of them does not match the property in my class, it will be ignored.
////
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


//        FileReader reader = new FileReader("src/main/resources/recentquotes.json");
////        Gson gson = new Gson();
//
//        List<Map> mapList = gson.fromJson(reader , List.class);
//        int min=0;
//        int max= mapList.size()-1;
//        int j= (int) (Math.random()*(max-min+1)+min);
//
//        Map map = mapList.get(j);

//        System.out.println(map);

        //*****************************************************************************************************************

        FileOutputStream outputStream = new FileOutputStream("src/main/resources/addingFromClient.json" , true);
//        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, Arrays.toString("\"Hello\":\"world\"".getBytes())));
        outputStream.write("\"Helo\":\"wo77rld\"\r\n".getBytes());
        outputStream.flush();
        outputStream.close();

    }
}
