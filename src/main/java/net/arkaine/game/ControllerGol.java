package net.arkaine.game;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import net.arkaine.game.component.GameCanvas;
import net.arkaine.game.thread.GameThread;

import static javafx.application.Platform.exit;

public class ControllerGol {

    private boolean isPlay = false;
    private int age = 0;
    private GameThread gameThread = new GameThread();

    @FXML
    private ImageView pauseImg;

    @FXML
    private GameCanvas gameofLife;

    @FXML
    private ImageView playImg;

    @FXML
    private Button playBtn;

    @FXML
    private void playAction(ActionEvent event) {
        if(isPlay){
            playBtn.setGraphic(playImg);
            isPlay = false;
        }else{
            if(!pauseImg.isVisible())pauseImg.setVisible(true);
            playBtn.setGraphic(pauseImg);
            isPlay = true;
        }
        event.consume();
    }

    private Task<Integer> task = new Task<Integer>() {
        @Override
        protected Integer call() throws Exception {
            int i;
            while(true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
                // Update the GUI on the JavaFX Application Thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(ControllerGol.this.isPlay)
                            gameofLife.updateGame();
                    }
                });
            }
        }
    };

    private Thread th = new Thread(task);

    @FXML
    private void stopAction(ActionEvent event) {
        gameofLife.init();
        event.consume();
    }

    @FXML
    private Button stopBtn;

    public ControllerGol(){
        gameThread.setGameCanvas(gameofLife);
        th.start();
    }

    @FXML
    public void clickCanvas(MouseEvent mouseEvent) {
        int x = (int)Math.round(mouseEvent.getX());
        int y = (int)Math.round(mouseEvent.getY());
        gameofLife.getGraphicsContext2D().getPixelWriter().setColor(x,y, Color.BLUE);
    }
}
