package net.arkaine.game;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

public final class GameofLifeApplication extends Application
{

    public GameofLifeApplication()
    {
    }

    @Override
    public void start(final Stage stage)
    {        URL url = getClass().getResource("/gol.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent mainParent = null;
        try {
            mainParent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Game Of Life - with pressure and colors !");
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        Dimension size
                = Toolkit.getDefaultToolkit().getScreenSize();
        int width = size.width;
        int height = size.height;
        Scene scene = new Scene(mainParent, width, height);
       // stage.setFullScreen(true);
        stage.setScene(scene);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>
                () {

            @Override
            public void handle(KeyEvent t) {
                if(t.getCode()== KeyCode.ESCAPE)
                {
                    System.exit(0);

                }
            }
        });
        stage.show();
    }
}