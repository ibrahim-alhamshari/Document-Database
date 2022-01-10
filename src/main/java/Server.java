import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Server extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TextArea textArea = new TextArea();

        Scene scene = new Scene(new ScrollPane(textArea) , 450 , 200);
        primaryStage.setTitle("Final app");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
