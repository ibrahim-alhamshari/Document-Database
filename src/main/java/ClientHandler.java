import model.User;
import services.UserService;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    public static List<ClientHandler> clientHandlerList = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientUsername;
    private boolean isAdmin;
    private UserService userServices;


    public ClientHandler(Socket socket) throws IOException {

        this.socket = socket;

        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());

        sendMessageToClients("A new user has entered the system");

        userServices = UserService.getInstance();
        clientHandlerList.add(this);

//        login();
    }


    @Override
    public void run() {

        while (socket.isConnected()) {
            try {
//                sendMessageToClients(clientUsername + ": " + msg);
                login();
            } catch (IOException e) {
                try {
                    removeClientHandler();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }

    }


    public void sendMessageToClients(String messageToClients) throws IOException {

        for (ClientHandler clientHandler : clientHandlerList) {

            if (!clientHandler.clientUsername.equals(this.clientUsername)) {
                clientHandler.bufferedWriter.write(messageToClients);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
//                dataOutputStream.writeUTF(messageToClients);
//                dataOutputStream.flush();
            }
        }

    }



    public void login() throws IOException {
        this.bufferedWriter.write("Enter your username:");

        while (true) {
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

            String username = bufferedReader.readLine();

            this.bufferedWriter.write("Enter your password: ");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            String password = bufferedReader.readLine();


            if (username==null || password==null) {
                this.bufferedWriter.write("Please fill all fields !");
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
                continue;
            }

            boolean inUserFound = userServices.isUserFound(username, password);

            if (inUserFound) {
                this.clientUsername = username;
                break;
            }

            dataOutputStream.writeUTF("Incorrect username or password !\n");
            this.bufferedWriter.write("Re-Enter your username:");
        }

        if (userServices.isAdmin(clientUsername)) {
            adminProcesses();
        } else {
            userProcesses();
        }

    }



    public void userProcesses() throws IOException {
        dataOutputStream.writeUTF("Success login ...\n" + "User processes: ");


    }



    public void adminProcesses() throws IOException {

        dataOutputStream.writeUTF("Success login ...\n" + "Admin processes: \n"
                + "1. \"Get users\"\n"
                + "2. \"Add new users\"\n"
                + "3. \"Update user info\"\n"
                + "4. \"Remove users\"\n"
                + "Type (exactly) the process that you want in the query field:\n");

        String query =  bufferedReader.readLine();

        switch (query.toLowerCase().replace("\"" , "")){
            case "get users":
                dataOutputStream.writeUTF("\"Get users\": Type the user name in the field");
                getUser();
                break;

            case "add new users":
                dataOutputStream.writeUTF("\"Add new users\": ");
                addNewUser();
                break;

            case "update user info":
                dataOutputStream.writeUTF("\"Update user info\": ");
                updateUser();
                break;

            case "remove users":
                dataOutputStream.writeUTF("\"Remove users\": ");
                removeUser();
                break;

        }

    }



    public void getUser() throws IOException {

        String username = dataInputStream.readUTF();
        String password = dataInputStream.readUTF();
        String email = dataInputStream.readUTF();

        User user = userServices.getUserByUserName(username);
        if(user==null){
            dataOutputStream.writeUTF("There is no user with this username: " +username);
            return;
        }

        dataOutputStream.writeUTF("User info:\n" + "Username: " + username + "\n Email: " + user.getEmail() + "\nRegistration date: " + user.getRegisterDate());
    }



    public void addNewUser() throws IOException {

        while (true){
            this.bufferedWriter.write("Enter the username for the new user: ");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            String username = this.bufferedReader.readLine();

            this.bufferedWriter.write("Enter the password for the new user: ");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            String password = this.bufferedReader.readLine();

            this.bufferedWriter.write("Enter the email for the new user: ");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            String email = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);
            if(user==null){
                dataOutputStream.writeUTF("There is no user with this username: " + username);
                continue;
            }

            this.bufferedWriter.write("Here are the user details: \n Username: "+ user.getUsername()+ "\n Email: " + user.getEmail() +"\nEnter the email that you are going to update: ");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
//            String email = this.bufferedReader.readLine();

            dataOutputStream.writeUTF("successfully update user with username \""+ username +"\"\n");
            break;
        }


        String username = dataInputStream.readUTF();
        String password = dataInputStream.readUTF();
        String email = dataInputStream.readUTF();

        if(username.equals("") || password.equals("") || email.equals("")){
            dataOutputStream.writeUTF("Please fill all fields !");
            return;
        }

        long newUserId = userServices.getAllUsers().size();
        User user = new User(newUserId , LocalDateTime.now() , username , password , email);

        userServices.createUser(user);
        dataOutputStream.writeUTF("A new user with username \"" + username + "\" was created ...");

    }



    public void updateUser() throws IOException {

        while (true){
            this.bufferedWriter.write("Enter the username that you are going to update: ");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            String username = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);
            if(user==null){
                dataOutputStream.writeUTF("There is no user with this username: " + username);
                continue;
            }

            this.bufferedWriter.write("Here are the user details: \n Username: "+ user.getUsername()+ "\n Email: " + user.getEmail() +"\nEnter the email that you are going to update: ");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            String email = this.bufferedReader.readLine();

            user.setEmail(email);

            userServices.updateUser(user);
            dataOutputStream.writeUTF("successfully update user with username \""+ username +"\"\n");
            break;
        }

        this.bufferedWriter.write("Now you have to re-enter your details again to do more actions...\n \n");
        this.bufferedWriter.newLine();
        this.bufferedWriter.flush();
    }



    public void removeUser() throws IOException {

        while (true) {
            this.bufferedWriter.write("Enter the username that you are going to remove: ");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            String username = bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);
            if (user == null) {
                dataOutputStream.writeUTF("There is no user with this username: " + username);
                continue;
            }

            userServices.deleteUser(user);
            dataOutputStream.writeUTF("successfully delete user with username \" " + username +" \"...\n");
            break;
        }

        this.bufferedWriter.write("Now you have to re-enter your details again to do more actions...\n \n");
        this.bufferedWriter.newLine();
        this.bufferedWriter.flush();
    }



    public void removeClientHandler() throws IOException { //if the user left the chat.

        clientHandlerList.remove(this);
        sendMessageToClients("SERVER: " + clientUsername + " has left the chat!");

    }


}
