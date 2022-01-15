import java.net.Socket;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class Client extends Application {
    //IO stream
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;
    Button submit = new Button("submit");
    Button sendQuery = new Button("sendQuery");
    TextArea textArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        // Panel p to hold the label and text field

        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        HBox buttons = new HBox(submit, sendQuery);

        buttons.setSpacing(10);
        borderPane.setCenter(gridPane);
        borderPane.setBottom(buttons);
        BorderPane.setAlignment(buttons, Pos.CENTER);

        BorderPane.setMargin(buttons, new Insets(0, 0, 5, 120));

        gridPane.setAlignment(Pos.CENTER);

        Label label1 = new Label(" Username: ");
        Label label2 = new Label(" password: ");
        Label label3 = new Label(" Email or query: ");
        Label label4 = new Label(" OutPut Field: ");
        Label label5 = new Label(" Course: ");

        TextField username = new TextField();
        TextField password = new TextField();
        TextField emailOrQuery = new TextField();
        TextField course = new TextField();


        gridPane.add(label1, 0, 0);
        gridPane.add(label2, 0, 1);
        gridPane.add(label3, 0, 2);
        gridPane.add(label4, 0, 3);
        gridPane.add(label5, 0, 4);

        gridPane.add(username, 1, 0);
        gridPane.add(password, 1, 1);
        gridPane.add(emailOrQuery, 1, 2);
        gridPane.add(textArea, 1, 3);
        gridPane.add(course, 1, 4);
        course.setDisable(true);

        gridPane.setAlignment(Pos.CENTER);

        // create a scene and add it to the screen
        Scene scene = new Scene(borderPane, 1000, 600);
        primaryStage.setTitle("Exercise 2");
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer();

        emailOrQuery.setDisable(true);
        final boolean[] isLogin = {false};

        submit.setOnAction(e -> {
            try {


                if(!isLogin[0]){
                    toServer.writeUTF(username.getText());
                    toServer.writeUTF(password.getText());
                    toServer.flush();

                    String message = fromServer.readUTF();
                    textArea.appendText(message + "\n");

                    if(message.startsWith("Success login")){
                        isLogin[0] = true;
                        username.setDisable(true);
                        password.setDisable(true);
//                        textArea.setText(null);
                        emailOrQuery.setDisable(false);
//                        toServer.writeUTF(emailOrQuery.getText());
                    }
                }else {
                    toServer.writeUTF(emailOrQuery.getText());
                    submit.setDisable(true);
                    username.setDisable(false);
                    password.setDisable(false);
                    username.setText(null);
                    password.setText(null);
                    emailOrQuery.setText(null);
                    String message = fromServer.readUTF();
                    textArea.appendText(message + "\n");

                }


            } catch (IOException ex) {
                System.err.println(ex);
            }
        });


        sendQuery.setOnAction(e -> {
            try {
                toServer.writeUTF(username.getText());
                toServer.writeUTF(password.getText());
                toServer.writeUTF(emailOrQuery.getText());
                textArea.appendText(fromServer.readUTF() + "\n");

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }


    public void connectToServer() {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 6060);
            // Socket socket = new Socket("130.254.204.36", 8000);
            // Socket socket = new Socket("drake.Armstrong.edu", 8000);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());

            textArea.appendText(fromServer.readUTF());
        } catch (IOException ex) {
            textArea.appendText("Connection refused!" + '\n');
        }
    }


}