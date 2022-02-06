import model.Address;
import model.Notification;
import model.Task;
import model.User;
import queue.Queue;
import servicesLayer.AddressServices;
import servicesLayer.NotificationServices;
import servicesLayer.UserServices;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerHandler implements Runnable {

    public static List<ServerHandler> serverHandlerList = new ArrayList<>();
    public static Queue<String> queue= new Queue<String>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private UserServices userServices;
    private AddressServices addressServices;
    private NotificationServices notificationServices;


    public ServerHandler(Socket socket) throws IOException {

        this.socket = socket;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }

        userServices = UserServices.getInstance();
        addressServices = AddressServices.getInstance();
        notificationServices = NotificationServices.getInstance();

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

                serverHandlerList.add(this);

                User user = userServices.getUserByUserName(username);
                if(userServices.isAdmin(user) && password.equals("admin")){
                    changeAdminPassword(password);
                }

                writeToClient("Success login ...");
                break;
            }

            writeToClient("Incorrect username or password !");
            message = "Re-Enter your username:";
        }

        while (true){
            masterNode();
        }

    }


    private void changeAdminPassword(String oldPassword) throws IOException {

        User admin = userServices.getUserByUserName(clientUsername);
        if (admin.getPassword().equals(oldPassword)) {
            writeToClient("This is the default password, You have to change it.\nEnter a new password:");

            while (true){
                String newPassword = bufferedReader.readLine();

                if(newPassword.length()<8){
                    writeToClient("Very short password. Re-enter the password again:");
                    continue;
                }

                admin.setPassword(newPassword);
                userServices.updateUser(admin);
                writeToClient("Successfully update the password");
                break;
            }

        }

    }



    public void masterNode() throws IOException {

        User user = userServices.getUserByUserName(clientUsername);
        boolean isAdmin = userServices.isAdmin(user);

        if(isAdmin) {
            writeToClient("Admin processes: \n"
                    + "1. \"Get user info\"\n"
                    + "2. \"Add new user\"\n"
                    + "3. \"Update user info\"\n"
                    + "4. \"Remove user\"\n"
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
                    break;

                case "add new user":
                    writeToClient("\"Add new user\": ");
                    createNewUser();
                    break;

                case "update user info":
                    writeToClient("\"Update user info\": ");
                    updateUserInfo();
                    break;

                case "remove user":
                    writeToClient("\"Remove users\": ");
                    removeUser();
                    break;

                case "assign task to user":
                    writeToClient("\"Assign task to user:\"");
                    createTask();
                    break;

                default:
                    writeToClient("Your input does not match any case, please try again");
            }
        }else {
            writeToClient("You can type your name or any username to get info.");
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
        }
    }



    public void getUserInfoNode1() throws IOException {

        writeToClient("Write any username to get information about it (Node 1):");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                writeToClient("There is no user with this username: " + username);
                writeToClient("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = user.getTasks();

            writeToClient("User info:\n" + "Username: " + username
                    +"\nRole: " + user.getRole()
                    + "\nEmail: " + user.getEmail()
                    + "\nRegistration date: " + user.getRegisterDate());

            Address address = user.getAddress();

            if(Objects.nonNull(address)){
                writeToClient("Country: " + address.getCountry() + "\nCity: " +address.getCity() + "\npostalCode: " +address.getPostalCode());
            }

            if(Objects.nonNull(userTasks) && userTasks.size()>0) {
                writeToClient("\nHere are the tasks for " + username + ":");

                for (Task task : userTasks) {
                    writeToClient("Subject: " + task.getSubject());
                    writeToClient("Details: " + task.getDescription());
                    writeToClient("\n");
                }
            }

            break;
        }

        writeToClient("Now the processes for getting a user has finished, you can choose another options...\n********************************");
    }


    public void getUserInfoNode2() throws IOException {

        writeToClient("Write any username to get information about it (Node 2):");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                writeToClient("There is no user with this username: " + username);
                writeToClient("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = user.getTasks();

            writeToClient("User info:\n" + "Username: " + username
                    +"\nRole: " + user.getRole()
                    + "\nEmail: " + user.getEmail()
                    + "\nRegistration date: " + user.getRegisterDate());

            Address address = user.getAddress();

            if(Objects.nonNull(address)){
                writeToClient("Country: " + address.getCountry() + "\nCity: " +address.getCity() + "\npostalCode: " +address.getPostalCode());
            }

            if(Objects.nonNull(userTasks) && userTasks.size()>0) {
                writeToClient("\nHere are the tasks for " + username + ":");

                for (Task task : userTasks) {
                    writeToClient("Subject: " + task.getSubject());
                    writeToClient("Details: " + task.getDescription());
                    writeToClient("\n");
                }
            }

            break;
        }

        writeToClient("Now the processes for getting a user has finished, you can choose another options...\n********************************");
    }


    public void getUserInfoNode3() throws IOException {

        writeToClient("Write any username to get information about it (Node 3):");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                writeToClient("There is no user with this username: " + username);
                writeToClient("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = user.getTasks();

            writeToClient("User info:\n" + "Username: " + username
                    +"\nRole: " + user.getRole()
                    + "\nEmail: " + user.getEmail()
                    + "\nRegistration date: " + user.getRegisterDate());

            Address address = user.getAddress();

            if(Objects.nonNull(address)){
                writeToClient("Country: " + address.getCountry() + "\nCity: " +address.getCity() + "\npostalCode: " +address.getPostalCode());
            }

            if(Objects.nonNull(userTasks) && userTasks.size()>0) {
                writeToClient("\nHere are the tasks for " + username + ":");

                for (Task task : userTasks) {
                    writeToClient("Subject: " + task.getSubject());
                    writeToClient("Details: " + task.getDescription());
                    writeToClient("\n");
                }
            }

            break;
        }

        writeToClient("Now the processes for getting a user has finished, you can choose another options...\n********************************");
    }


    public void getUserInfoNode4() throws IOException {

        writeToClient("Write any username to get information about it (Node 4):");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                writeToClient("There is no user with this username: " + username);
                writeToClient("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = user.getTasks();

            writeToClient("User info:\n" + "Username: " + username
                    +"\nRole: " + user.getRole()
                    + "\nEmail: " + user.getEmail()
                    + "\nRegistration date: " + user.getRegisterDate());

            Address address = user.getAddress();

            if(Objects.nonNull(address)){
                writeToClient("Country: " + address.getCountry() + "\nCity: " +address.getCity() + "\npostalCode: " +address.getPostalCode());
            }

            if(Objects.nonNull(userTasks) && userTasks.size()>0) {
                writeToClient("\nHere are the tasks for " + username + ":");

                for (Task task : userTasks) {
                    writeToClient("Subject: " + task.getSubject());
                    writeToClient("Details: " + task.getDescription());
                    writeToClient("\n");
                }
            }

            break;
        }

        writeToClient("Now the processes for getting a user has finished, you can choose another options...\n********************************");
    }




    public void createNewUser() throws IOException {

        String message ="Enter the username for the new user: ";
        while (true){

            writeToClient(message);
            String username = this.bufferedReader.readLine();

            User userFromDB = userServices.getUserByUserName(username);

            if(Objects.nonNull(userFromDB.getId())){
                writeToClient("This username already exist !!");
                message ="Re-enter the username for the new user";
                continue;
            }


            writeToClient("Enter the email for the new user: ");
            String email = this.bufferedReader.readLine();

            if(username.equals("")|| email.equals("")){
                writeToClient("You need to fill all the fields");
                message = "Re-enter the username for the new user";
                continue;
            }


            writeToClient("Enter the role for the new user (ADMIN , USER): ");
            String roleString = this.bufferedReader.readLine();

            if(!roleString.equalsIgnoreCase("ADMIN") && !roleString.equalsIgnoreCase("USER")){
                writeToClient("Incorrect user role. Fill the fields again");
                message = "Re-enter the username for the new user";
                continue;
            }

            String password=null;

            if(roleString.equalsIgnoreCase("USER")){
                writeToClient("Enter the password for the new user: ");

                while (true){
                    password  = this.bufferedReader.readLine();

                    if(password.length()>=8){
                        break;
                    }

                    writeToClient("Very short password. Re-enter the password again");
                }

            }



            User.Role role = null;

            if(roleString.equalsIgnoreCase("ADMIN")){
                role= User.Role.ADMIN;
            }else {
                role = User.Role.USER;
            }

            User newUser = new User(username , password , email , role);

            userServices.createUser(newUser);

            informClientByChanges(username , "Create user");
            writeToClient("Successfully create a new user with username: '" + username + "'\n");
            writeToClient("Now the processes for create a new user has finished, you can choose another options...\n********************************");

            break;
        }

    }


    public void createTask() throws IOException{

        String message ="Enter a username to assign task to it: ";
        while (true){

            writeToClient(message);
            String username = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);

            if(Objects.isNull(user.getId())){
                writeToClient("This username is not exist !!");
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

            Task task = new Task(subject , description);
            List<Task> taskList = user.getTasks();

            if(Objects.isNull(taskList)){
                taskList = new ArrayList<>();
            }
            taskList.add(task);

            user.setTasks(taskList);

            userServices.updateUser(user);

            informClientByChanges(user.getUsername() , "Create task");

            writeToClient("Successfully assigned a task for the user: '" + username + "'\n");
            writeToClient("Now the processes for create a task has finished, you can choose another options...\n ********************************");

            informAllClients("***** A new task has assigned to " + user.getUsername() + " *****");
            break;
        }

    }


    public void updateUserInfo() throws IOException {

        while (true){
            writeToClient("Enter the username that you are going to update: ");
            String username = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);
            if(Objects.isNull(user.getId())){
                writeToClient("There is no user with this username: " + username);
                continue;
            }

            writeToClient("Here are the user details: \n Username: "+ user.getUsername()
                    + "\n Email: " + user.getEmail() +"\nEnter the email that you are going to update: ");
            String email = this.bufferedReader.readLine();

            if(email.equals("")){
                writeToClient("You need to fill the email!!");
                continue;
            }
            user.setEmail(email);

            Address address = updateUserAddress(); // Get the address from the client
            user.setAddress(address);

            userServices.updateUser(user);
            writeToClient("successfully update user with username \""+ username +"\"\n");

            informClientByChanges(user.getUsername() , "Update");

            break;
        }

        writeToClient("Now the processes of updating a user info has finished, you can choose another options...\n********************************");
    }


    public Address updateUserAddress() throws IOException {

        writeToClient("Update address. Select Address:");
        List<Address> addresses = AddressServices.getInstance().getAllAddresses();

        while (true) {
            for (Address address : addresses) {
                writeToClient(address.getCountry());
            }

            String country = this.bufferedReader.readLine();
            Address address = addressServices.getAddressByCountryName(country);

            if (Objects.isNull(address.getId())) {
                writeToClient("Incorrect country name. \nSelect an address again");
                continue;
            }

            return address;
        }
    }


    public void removeUser() throws IOException {
        String message = "Enter the username that you are going to remove: ";

        while (true) {
            writeToClient(message);
            String username = bufferedReader.readLine();

            if(username.equalsIgnoreCase("admin")){
                writeToClient("The Admin can not be deleted!");
                message= "Re-enter a username to remove it";
                continue;
            }

            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                writeToClient("There is no user with this username: " + username);
                continue;
            }

            userServices.removeUser(user);
            writeToClient("successfully delete user with username \" " + username +" \"...\n");
            informClientByChanges(user.getUsername() , "Delete");

            break;
        }

        writeToClient("Now the processes of removing a user has finished, you can choose another options...\n********************************");

    }


    public void removeClientHandler() throws IOException { //if the user left the website.
        serverHandlerList.remove(this);
        sendMessageToClients("SERVER: " + clientUsername + " has left the party!");
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


    public void informAllClients(String message){
        serverHandlerList.forEach(e->{
            if(!e.clientUsername.equals(this.clientUsername)){
                e.writeToClient(message);
            }
        });
    }


    public void informClientByChanges(String _username, String process) {
        serverHandlerList.forEach(e -> {
            if (e.clientUsername.equalsIgnoreCase(_username)) {
                e.writeToClient("Your data have updated");

                try {
                    Notification notification = new Notification(process, e.userServices.getUserByUserName(_username));
                    notificationServices.createNotification(notification);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


}
