import model.Task;
import model.User;
import queue.Queue;
import services.UserService;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

public class ServerHandler implements Runnable {

    public static List<ServerHandler> serverHandlerList = new ArrayList<>();
    public static Queue<String> queue= new Queue<String>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientUsername;
    private boolean isAdmin;
    private UserService userServices;


    public ServerHandler(Socket socket) throws IOException {

        this.socket = socket;

        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());

        userServices = UserService.getInstance();
        serverHandlerList.add(this);

    }


    @Override
    public void run() {

        while (socket.isConnected()) {
            try {

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

        for (ServerHandler serverHandler : serverHandlerList) {
            if(Objects.nonNull(this.clientUsername)) {

                if (!serverHandler.clientUsername.equals(this.clientUsername)) {
                    serverHandler.bufferedWriter.write(messageToClients);
                    serverHandler.bufferedWriter.newLine();
                    serverHandler.bufferedWriter.flush();
                }

            }
        }

    }



    public void login() throws IOException {
        String message ="Enter your username:";

        while (true) {

            writeToClient(message);

            String username = bufferedReader.readLine();

            writeToClient("Enter your password: ");
            String password = bufferedReader.readLine();

            if (username.equals("") || password.equals("")) {
                writeToClient("Please fill all fields !");
                message ="Re-enter your username";
                continue;
            }

            boolean isUserFound = userServices.isUserFound(username, password);

            if (isUserFound) {
                this.clientUsername = username;
                writeToClient("Success login ...");
                break;
            }

            writeToClient("Incorrect username or password !");
            message = "Re-Enter your username:";
        }



        if (userServices.isAdmin(clientUsername)) {
            while (true)
                adminProcesses();
        } else {
            while (true)
                userProcesses();
        }

    }



    public void userProcesses() throws IOException {
        writeToClient("\n" + "User processes: You can only read information about users from the system ...");
        getUserInfoNode1();
    }



    public void adminProcesses() throws IOException {
        boolean isAdmin =userServices.isAdmin(clientUsername);

        if(isAdmin) {
            writeToClient("Admin processes: \n"
                    + "1. \"Get user info\"\n"
                    + "2. \"Add new users\"\n"
                    + "3. \"Update user info\"\n"
                    + "4. \"Remove users\"\n"
                    + "5. \"Assign task to user\"\n"
                    + "Type (exactly) the process that you want to execute:");

            String query = bufferedReader.readLine();

            switch (query.toLowerCase().replace("\"", "")) {
                case "get user info":
                    writeToClient("\"Get user info\": ");
                    while (true) {
                        if (!queue.isContain("getUserInfoNode1")) {
                            queue.enqueue("getUserInfoNode1");
                            getUserInfoNode1();
                            break;
                        } else if (!queue.isContain("getUserInfoNode2")) {
                            queue.enqueue("getUserInfoNode2");
                            getUserInfoNode2();
                            break;
                        } else if (!queue.isContain("getUserInfoNode3")) {
                            queue.enqueue("getUserInfoNode3");
                            getUserInfoNode3();
                            break;
                        } else if (!queue.isContain("getUserInfoNode4")) {
                            queue.enqueue("getUserInfoNode4");
                            getUserInfoNode4();
                            break;
                        } else {
                            queue.dequeue();
                        }
                    }
                case "add new users":
                    writeToClient("\"Add new users\": ");
                    createNewUser();
                    break;

                case "update user info":
                    writeToClient("\"Update user info\": ");
                    updateUser();
                    break;

                case "remove users":
                    writeToClient("\"Remove users\": ");
                    removeUser1();
                    break;

                case "assign task to user":
                    writeToClient("\"Assign task to user:\"");
                    createTask();
                    break;

                default:
                    writeToClient("Your input does not match any case, please try again");
            }
        }else {


        }
    }



    public void getUserInfoNode1() throws IOException {

        writeToClient("Write any username to get information about it1:");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (user == null) {
                writeToClient("There is no user with this username: " + username);
                writeToClient("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = userServices.getUserTasks(username);

            writeToClient("User info:\n" + "Username: " + username + "\nEmail: " + user.getEmail() + "\nRegistration date: " + user.getRegisterDate());

            if(userTasks.size()>0){
                writeToClient("\nHere are the tasks for "+username+ ":");
            }

            for (Task task: userTasks){
                writeToClient("Subject: " +task.getSubject());
                writeToClient("Details: " + task.getDescription());
                writeToClient("\n");
            }

            break;
        }

        writeToClient("Now the processes for getting a user has finished, you can choose another options...");
    }


    public void getUserInfoNode2() throws IOException {

        writeToClient("Write any username to get information about it2:");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (user == null) {
                writeToClient("There is no user with this username: " + username);
                writeToClient("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = userServices.getUserTasks(username);

            writeToClient("User info:\n" + "Username: " + username + "\nEmail: " + user.getEmail() + "\nRegistration date: " + user.getRegisterDate());

            if(userTasks.size()>0){
                writeToClient("\nHere are the tasks for "+username+ ":");
            }

            for (Task task: userTasks){
                writeToClient("Subject: " +task.getSubject());
                writeToClient("Details: " + task.getDescription());
                writeToClient("\n");
            }

            break;
        }

        writeToClient("Now the processes for getting a user has finished, you can choose another options...");
    }



    public void getUserInfoNode3() throws IOException {

        writeToClient("Write any username to get information about it3:");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (user == null) {
                writeToClient("There is no user with this username: " + username);
                writeToClient("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = userServices.getUserTasks(username);

            writeToClient("User info:\n" + "Username: " + username + "\nEmail: " + user.getEmail() + "\nRegistration date: " + user.getRegisterDate());

            if(userTasks.size()>0){
                writeToClient("\nHere are the tasks for "+username+ ":");
            }

            for (Task task: userTasks){
                writeToClient("Subject: " +task.getSubject());
                writeToClient("Details: " + task.getDescription());
                writeToClient("\n");
            }

            break;
        }

        writeToClient("Now the processes for getting a user has finished, you can choose another options...");
    }


    public void getUserInfoNode4() throws IOException {

        writeToClient("Write any username to get information about it4:");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (user == null) {
                writeToClient("There is no user with this username: " + username);
                writeToClient("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = userServices.getUserTasks(username);

            writeToClient("User info:\n" + "Username: " + username + "\nEmail: " + user.getEmail() + "\nRegistration date: " + user.getRegisterDate());

            if(userTasks.size()>0){
                writeToClient("\nHere are the tasks for "+username+ ":");
            }

            for (Task task: userTasks){
                writeToClient("Subject: " +task.getSubject());
                writeToClient("Details: " + task.getDescription());
                writeToClient("\n");
            }

            break;
        }

        writeToClient("Now the processes for getting a user has finished, you can choose another options...");
    }


    public void createNewUser() throws IOException {

        String message ="Enter the username for the new user: ";
        while (true){

            writeToClient(message);
            String username = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);

            if(user!=null){
                writeToClient("This username already exist !!");
                message ="Re-enter the username for the new user";
                continue;
            }

            writeToClient("Enter the password for the new user: ");
            String password = this.bufferedReader.readLine();

            writeToClient("Enter the email for the new user: ");
            String email = this.bufferedReader.readLine();

            if(username.equals("") || password.equals("") || email.equals("")){
                writeToClient("You need to fill all the fields");
                message = "Re-enter the username for the new user";
                continue;
            }

            userServices.createUser(new User(LocalDateTime.now() , username , password , email));
            writeToClient("Successfully create a new user with username: '" + username + "'\n");
            writeToClient("Now the processes for create a new user has finished, you can choose another options...");

            break;
        }

    }


    public void createTask() throws IOException{

        String message ="Enter a username to assign task to it: ";
        while (true){

            writeToClient(message);
            String username = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);

            if(user == null){
                writeToClient("This username not exist !!");
                message ="Re-enter the username for a new task";
                continue;
            }

            writeToClient("Enter the subject for the task: ");
            String subject = this.bufferedReader.readLine();

            writeToClient("Enter the description for the task: ");
            String description = this.bufferedReader.readLine();

            if(username.equals("") || subject.equals("") || description.equals("")){
                writeToClient("You need to fill all the fields");
                message = "Re-enter the username for the task:";
                continue;
            }

            userServices.createTask(new Task(subject , description , user));

            writeToClient("Successfully assigned a task for the user: '" + username + "'\n");
            writeToClient("Now the processes for create a task has finished, you can choose another options...");

            break;
        }

    }



    public void updateUser() throws IOException {

        while (true){
            writeToClient("Enter the username that you are going to update: ");
            String username = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);
            if(user==null){
                writeToClient("There is no user with this username: " + username + "\n");
                continue;
            }

            writeToClient("Here are the user details: \n Username: "+ user.getUsername()+ "\n Email: " + user.getEmail() +"\nEnter the email that you are going to update: ");
            String email = this.bufferedReader.readLine();

            if(email.equals("")){
                writeToClient("You need to fill the email!!");
                continue;
            }
            user.setEmail(email);

            userServices.updateUser(user);
            writeToClient("successfully update user with username \""+ username +"\"\n");
            break;
        }

        writeToClient("Now the processes of updating a user info has finished, you can choose another options...");
    }



    public void removeUser1() throws IOException {

        while (true) {
            writeToClient("Enter the username that you are going to remove: ");
            String username = bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);
            if (user == null) {
                writeToClient("There is no user with this username: " + username + "\n");
                continue;
            }

            userServices.removeUser(user);
            writeToClient("successfully delete user with username \" " + username +" \"...\n");
            break;
        }

        writeToClient("Now the processes of removing a user has finished, you can choose another options...");

    }


    public void removeUser2() throws IOException {

        while (true) {
            writeToClient("Enter the username that you are going to remove: ");
            String username = bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);
            if (user == null) {
                writeToClient("There is no user with this username: " + username + "\n");
                continue;
            }

            userServices.removeUser(user);
            writeToClient("successfully delete user with username \" " + username +" \"...\n");
            break;
        }

        writeToClient("Now the processes of removing a user has finished, you can choose another options...");

    }



    public void removeClientHandler() throws IOException { //if the user left the website.
        serverHandlerList.remove(this);
        sendMessageToClients("SERVER: " + clientUsername + " has left the chat!");
    }


    public void writeToClient(String message){
        try {
            this.bufferedWriter.write(message );
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
