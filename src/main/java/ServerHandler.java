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
                    removeUserFromServerHandlerList();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                break;

            }
        }

    }


    public void login() throws IOException {
        String message ="Enter your username:";

        while (true) {

            sendMessageToUser(message);

            String username = bufferedReader.readLine();

            sendMessageToUser("Enter your password: ");
            String password = bufferedReader.readLine();

            if (username.equals("") || password.equals("")) {
                sendMessageToUser("Please fill all fields !");
                message ="Re-enter your username";
                continue;
            }

            boolean isUserFound = userServices.isUserFound(username, password);

            if (isUserFound) {
                this.clientUsername = username;

                serverHandlerList.add(this); //Once the user is logined, it's information must be added to the handler list

                User user = userServices.getUserByUserName(username);
                if(userServices.isAdmin(user) && password.equals("admin")){
                    changeAdminPassword(password);
                }

                sendMessageToUser("Success login ...");
                break;
            }

            sendMessageToUser("Incorrect username or password !");
            message = "Re-Enter your username:";
        }

        while (true){
            masterNode();
        }

    }


    private void changeAdminPassword(String oldPassword) throws IOException {

        User admin = userServices.getUserByUserName(clientUsername);
        if (admin.getPassword().equals(oldPassword)) {
            sendMessageToUser("This is the default password, You have to change it.\nEnter a new password:");

            while (true){
                String newPassword = bufferedReader.readLine();

                if(newPassword.length()<8){
                    sendMessageToUser("Very short password. Re-enter the password again:");
                    continue;
                }

                admin.setPassword(newPassword);
                userServices.updateUser(admin);
                sendMessageToUser("Successfully update the password");
                break;
            }

        }

    }



    public void masterNode() throws IOException {

        User user = userServices.getUserByUserName(clientUsername);
        boolean isAdmin = userServices.isAdmin(user);

        if(isAdmin) {
            sendMessageToUser("Admin processes: \n"
                    + "1. \"Get user info\"\n"
                    + "2. \"Add new user\"\n"
                    + "3. \"Update user info\"\n"
                    + "4. \"Remove user\"\n"
                    + "5. \"Assign task to user\"\n"
                    + "Type (exactly) the process that you want to execute:");

            String query = bufferedReader.readLine();

            switch (query.toLowerCase().replace("\"", "")) {
                case "get user info":
                    sendMessageToUser("\"Get user info\": ");
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
                    sendMessageToUser("\"Add new user\": ");
                    createNewUser();
                    break;

                case "update user info":
                    sendMessageToUser("\"Update user info\": ");
                    updateUserInfo();
                    break;

                case "remove user":
                    sendMessageToUser("\"Remove users\": ");
                    removeUser();
                    break;

                case "assign task to user":
                    sendMessageToUser("\"Assign task to user:\"");
                    createTask();
                    break;

                default:
                    sendMessageToUser("Your input does not match any case, please try again");
            }
        }else {
            sendMessageToUser("You can type your name or any username to get info.");
            sendMessageToUser("\"Get user info\": ");
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

        sendMessageToUser("Write any username to get information about it (Node 1):");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                sendMessageToUser("There is no user with this username: " + username);
                sendMessageToUser("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = user.getTasks();

            sendMessageToUser("User info:\n" + "Username: " + username
                    +"\nRole: " + user.getRole()
                    + "\nEmail: " + user.getEmail()
                    + "\nRegistration date: " + user.getRegisterDate());

            Address address = user.getAddress();

            if(Objects.nonNull(address)){
                sendMessageToUser("Country: " + address.getCountry() + "\nCity: " +address.getCity() + "\npostalCode: " +address.getPostalCode());
            }

            if(Objects.nonNull(userTasks) && userTasks.size()>0) {
                sendMessageToUser("\nHere are the tasks for " + username + ":");

                for (Task task : userTasks) {
                    sendMessageToUser("Subject: " + task.getSubject());
                    sendMessageToUser("Details: " + task.getDescription());
                    sendMessageToUser("\n");
                }
            }

            break;
        }

        sendMessageToUser("Now the processes for getting a user has finished, you can choose another options...\n********************************");
    }


    public void getUserInfoNode2() throws IOException {

        sendMessageToUser("Write any username to get information about it (Node 2):");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                sendMessageToUser("There is no user with this username: " + username);
                sendMessageToUser("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = user.getTasks();

            sendMessageToUser("User info:\n" + "Username: " + username
                    +"\nRole: " + user.getRole()
                    + "\nEmail: " + user.getEmail()
                    + "\nRegistration date: " + user.getRegisterDate());

            Address address = user.getAddress();

            if(Objects.nonNull(address)){
                sendMessageToUser("Country: " + address.getCountry() + "\nCity: " +address.getCity() + "\npostalCode: " +address.getPostalCode());
            }

            if(Objects.nonNull(userTasks) && userTasks.size()>0) {
                sendMessageToUser("\nHere are the tasks for " + username + ":");

                for (Task task : userTasks) {
                    sendMessageToUser("Subject: " + task.getSubject());
                    sendMessageToUser("Details: " + task.getDescription());
                    sendMessageToUser("\n");
                }
            }

            break;
        }

        sendMessageToUser("Now the processes for getting a user has finished, you can choose another options...\n********************************");
    }


    public void getUserInfoNode3() throws IOException {

        sendMessageToUser("Write any username to get information about it (Node 3):");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                sendMessageToUser("There is no user with this username: " + username);
                sendMessageToUser("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = user.getTasks();

            sendMessageToUser("User info:\n" + "Username: " + username
                    +"\nRole: " + user.getRole()
                    + "\nEmail: " + user.getEmail()
                    + "\nRegistration date: " + user.getRegisterDate());

            Address address = user.getAddress();

            if(Objects.nonNull(address)){
                sendMessageToUser("Country: " + address.getCountry() + "\nCity: " +address.getCity() + "\npostalCode: " +address.getPostalCode());
            }

            if(Objects.nonNull(userTasks) && userTasks.size()>0) {
                sendMessageToUser("\nHere are the tasks for " + username + ":");

                for (Task task : userTasks) {
                    sendMessageToUser("Subject: " + task.getSubject());
                    sendMessageToUser("Details: " + task.getDescription());
                    sendMessageToUser("\n");
                }
            }

            break;
        }

        sendMessageToUser("Now the processes for getting a user has finished, you can choose another options...\n********************************");
    }


    public void getUserInfoNode4() throws IOException {

        sendMessageToUser("Write any username to get information about it (Node 4):");

        while (true) {

            String username = this.bufferedReader.readLine();
            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                sendMessageToUser("There is no user with this username: " + username);
                sendMessageToUser("Re-enter the username to see the details");
                continue;
            }

            List<Task> userTasks = user.getTasks();

            sendMessageToUser("User info:\n" + "Username: " + username
                    +"\nRole: " + user.getRole()
                    + "\nEmail: " + user.getEmail()
                    + "\nRegistration date: " + user.getRegisterDate());

            Address address = user.getAddress();

            if(Objects.nonNull(address)){
                sendMessageToUser("Country: " + address.getCountry() + "\nCity: " +address.getCity() + "\npostalCode: " +address.getPostalCode());
            }

            if(Objects.nonNull(userTasks) && userTasks.size()>0) {
                sendMessageToUser("\nHere are the tasks for " + username + ":");

                for (Task task : userTasks) {
                    sendMessageToUser("Subject: " + task.getSubject());
                    sendMessageToUser("Details: " + task.getDescription());
                    sendMessageToUser("\n");
                }
            }

            break;
        }

        sendMessageToUser("Now the processes for getting a user has finished, you can choose another options...\n********************************");
    }



    public void createNewUser() throws IOException {

        String message ="Enter the username for the new user: ";
        while (true){

            sendMessageToUser(message);
            String username = this.bufferedReader.readLine();

            User userFromDB = userServices.getUserByUserName(username);

            if(Objects.nonNull(userFromDB.getId())){
                sendMessageToUser("This username already exist !!");
                message ="Re-enter the username for the new user";
                continue;
            }


            sendMessageToUser("Enter the email for the new user: ");
            String email = this.bufferedReader.readLine();

            if(username.equals("")|| email.equals("")){
                sendMessageToUser("You need to fill all the fields");
                message = "Re-enter the username for the new user";
                continue;
            }


            sendMessageToUser("Enter the role for the new user (ADMIN , USER): ");
            String roleString = this.bufferedReader.readLine();

            if(!roleString.equalsIgnoreCase("ADMIN") && !roleString.equalsIgnoreCase("USER")){
                sendMessageToUser("Incorrect user role. Fill the fields again");
                message = "Re-enter the username for the new user";
                continue;
            }

            String password=null;

            if(roleString.equalsIgnoreCase("USER")){
                sendMessageToUser("Enter the password for the new user: ");

                while (true){
                    password  = this.bufferedReader.readLine();

                    if(password.length()>=8){
                        break;
                    }

                    sendMessageToUser("Very short password. Re-enter the password again");
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

            informUserByChanges(username);
            saveChangesToNotificationsDB(newUser , "Create user");

            sendMessageToUser("Successfully create a new user with username: '" + username + "'\n");
            sendMessageToUser("Now the processes for create a new user has finished, you can choose another options...\n********************************");

            break;
        }

    }


    public void createTask() throws IOException{

        String message ="Enter a username to assign task to it: ";
        while (true){

            sendMessageToUser(message);
            String username = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);

            if(Objects.isNull(user.getId())){
                sendMessageToUser("This username is not exist !!");
                message ="Re-enter the username for a new task";
                continue;
            }

            sendMessageToUser("Enter the subject for the task: ");
            String subject = this.bufferedReader.readLine();

            sendMessageToUser("Enter the description for the task: ");
            String description = this.bufferedReader.readLine();

            if(username.equals("") || subject.equals("") || description.equals("")){
                sendMessageToUser("You need to fill all the fields");
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

            informUserByChanges(user.getUsername());
            saveChangesToNotificationsDB(user , "Create task");

            sendMessageToUser("Successfully assigned a task for the user: '" + username + "'\n");
            sendMessageToUser("Now the processes for create a task has finished, you can choose another options...\n ********************************");

            break;
        }

    }


    public void updateUserInfo() throws IOException {

        while (true){
            sendMessageToUser("Enter the username that you are going to update: ");
            String username = this.bufferedReader.readLine();

            User user = userServices.getUserByUserName(username);
            if(Objects.isNull(user.getId())){
                sendMessageToUser("There is no user with this username: " + username);
                continue;
            }

            sendMessageToUser("Here are the user details: \n Username: "+ user.getUsername()
                    + "\n Email: " + user.getEmail() +"\nEnter the email that you are going to update: ");
            String email = this.bufferedReader.readLine();

            if(email.equals("")){
                sendMessageToUser("You need to fill the email!!");
                continue;
            }
            user.setEmail(email);

            Address address = updateUserAddress(); // Get the address from the client
            user.setAddress(address);

            userServices.updateUser(user);
            sendMessageToUser("successfully update user with username \""+ username +"\"\n");

            informUserByChanges(user.getUsername());
            saveChangesToNotificationsDB(user , "Delete");

            break;
        }

        sendMessageToUser("Now the processes of updating a user info has finished, you can choose another options...\n********************************");
    }


    public Address updateUserAddress() throws IOException {

        sendMessageToUser("Update address. Select Address:");
        List<Address> addresses = AddressServices.getInstance().getDataFromAddressList();

        while (true) {
            for (Address address : addresses) {
                sendMessageToUser(address.getCountry());
            }

            String country = this.bufferedReader.readLine();
            Address address = addressServices.getAddressByCountryName(country);

            if (Objects.isNull(address.getId())) {
                sendMessageToUser("Incorrect country name. \nSelect an address again");
                continue;
            }

            return address;
        }
    }


    public void removeUser() throws IOException {
        String message = "Enter the username that you are going to remove: ";

        while (true) {
            sendMessageToUser(message);
            String username = bufferedReader.readLine();

            if(username.equalsIgnoreCase("admin")){
                sendMessageToUser("The Admin can not be deleted!");
                message= "Re-enter a username to remove it";
                continue;
            }

            User user = userServices.getUserByUserName(username);
            if (Objects.isNull(user.getId())) {
                sendMessageToUser("There is no user with this username: " + username);
                continue;
            }

            userServices.removeUser(user);
            sendMessageToUser("successfully delete user with username \" " + username +" \"...\n");
            informUserByChanges(user.getUsername());
            saveChangesToNotificationsDB(user , "Delete");

            break;
        }

        sendMessageToUser("Now the processes of removing a user has finished, you can choose another options...\n********************************");

    }



    private void saveChangesToNotificationsDB(User user, String process){

        try {
            Notification notification = new Notification(user, process);
            notificationServices.createNotification(notification);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void removeUserFromServerHandlerList() throws IOException { //if the user logout from the website.
        serverHandlerList.remove(this);
        informAllUsers("SERVER: " + clientUsername + " has left the party!");
    }


    public void informUserByChanges(String _username) { // For any changes to the user's data.
        serverHandlerList.forEach(e -> {
            if (e.clientUsername.equalsIgnoreCase(_username)) {
                e.sendMessageToUser("**** Your data have updated ****");
            }
        });
    }


    public void informAllUsers(String message){ // Send message to all the connected clients.
        serverHandlerList.forEach(e->{
            if(!e.clientUsername.equals(this.clientUsername)){
                e.sendMessageToUser(message);
            }
        });
    }


    public void sendMessageToUser(String message){  //To send message from the server to the client
        try {
            this.bufferedWriter.write(message );
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
