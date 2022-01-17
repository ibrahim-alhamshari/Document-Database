import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;


public class Server {

    private ServerSocket serverSocket;


    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }



    public void startServer(){


        try {

            while (!serverSocket.isClosed()){

                Socket socket= new Socket();
                socket = serverSocket.accept();

                System.out.println("A new client has connected!");
                ServerHandler serverHandler = new ServerHandler(socket);
                Thread thread = new Thread(serverHandler);
                thread.start();

            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public void closeServer(){

        try {
            if(Objects.nonNull(this.serverSocket))
                serverSocket.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(6060);
        Server server = new Server(serverSocket);
        server.startServer();
    }

}
