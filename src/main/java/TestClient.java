import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
//    private String username;

    public TestClient(Socket socket , String username){

        try {
//            this.username = username;
            this.socket =socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            closeEverything(socket, bufferedWriter , bufferedReader);
        }

    }


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
//        System.out.println("You need to enter your username and your password to login.");
//        String _username = scanner.nextLine();
        Socket _socket = new Socket("localhost" , 6060);

        TestClient testClient = new TestClient(_socket , "_username");
        testClient.listenForMessage();
        testClient.sendMessage();
    }



    private void closeEverything(Socket socket , BufferedWriter _bufferedWriter , BufferedReader _bufferedReader){
        System.out.println("Everything is closed !!!!!!!!!!!!!!!!!!!!!!!");
        try {
            if(bufferedWriter != null)
                bufferedWriter.close();

            if(bufferedReader != null)
                bufferedReader.close();

            if(socket != null)
                socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }



    public void sendMessage(){
        try {
//            bufferedWriter.write(username);
//            bufferedWriter.newLine();
//            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        }catch (IOException e){
           closeEverything(socket , bufferedWriter , bufferedReader);
        }
    }


    public void listenForMessage(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                String msgFromServer;

                while (socket.isConnected()){
                    try {
                        msgFromServer = bufferedReader.readLine();
                        System.out.println(msgFromServer);
                    } catch (IOException e) {
                       closeEverything(socket , bufferedWriter , bufferedReader);
                       break;
                    }
                }
            }
        }).start();
    }
}
