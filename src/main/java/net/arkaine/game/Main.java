package
        net.arkaine.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL url = getClass().getResource("/gol.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
      //  fxmlLoader.setResources(ResourceBundle.getBundle("messages", Locale.getDefault()));
        Parent mainParent = fxmlLoader.load();

        primaryStage.setTitle("Assistant de réponse à une procédure");
        primaryStage.setTitle("Hello World");
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth()-20;
        int height = gd.getDisplayMode().getHeight()-120;
        primaryStage.setScene(new Scene(mainParent, width, height));
        primaryStage.show();
    }
    public static void main(String[] args) {

        launch(args);
    }

}