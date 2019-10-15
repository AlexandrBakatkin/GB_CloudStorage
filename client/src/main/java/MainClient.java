import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("CloudStorage");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        MainController controller = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(controller.getClose());
    }

    public static void main(String[] args) {
        launch(args);
    }
}