package net.arkaine.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        int width = gd.getDisplayMode().getWidth()-20;
        int height = gd.getDisplayMode().getHeight()-120;
        stage.setScene(new Scene(mainParent, width, height));
        stage.show();
    }
}