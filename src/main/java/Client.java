import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket){

        try {
            this.socket =socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            closeEverything(socket, bufferedWriter , bufferedReader);
        }

    }


    public static void main(String[] args) throws IOException {

        Socket _socket = new Socket("localhost" , 6060);

        Client client = new Client(_socket);
        client.listenForMessage();
        client.sendMessage();
    }



    private void closeEverything(Socket socket , BufferedWriter _bufferedWriter , BufferedReader _bufferedReader){
        System.out.println("THE SERVER HAS SHUTTING DOWN, YOU NEED TO RECONNECT AGAIN");
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
