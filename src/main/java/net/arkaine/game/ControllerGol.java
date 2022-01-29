package net.arkaine.game;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.awt.Color;
import net.arkaine.game.component.GameCanvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControllerGol {

    private boolean isPlay = false;

    @FXML
    private ImageView pauseImg;

    @FXML
    private GameCanvas gameofLife;

    @FXML
    private ImageView playImg;

    @FXML
    private Button playBtn;

    @FXML
    private ComboBox<String> colorComboBox;

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
            long speed = 1000;
            while(true) {
                try {
                    if( speedSld != null && speedSld.getValue() > 0)
                        speed=(long) speedSld.getValue();
                    Thread.sleep(speed);
                } catch (InterruptedException ie) {
                }
                // Update the GUI on the JavaFX Application Thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(ControllerGol.this.isPlay) {
                            try {
                                gameofLife.updateGame();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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
        th.setDaemon(true);
        th.start();
    }
    @FXML
    private Slider speedSld;

    @FXML
    public void drawCanvas(MouseEvent mouseEvent) {
        if(mouseEvent.isPrimaryButtonDown()) {
            int x = (int)Math.round(mouseEvent.getX());
            int y = (int)Math.round(mouseEvent.getY());
            Color selectedColor = new Color(0,0,255);
            if(colorComboBox.getSelectionModel().getSelectedItem() != null){
                List<Integer> colorInt = new ArrayList<>();
                Arrays.stream(colorComboBox.getSelectionModel().getSelectedItem().split(","))
                        .forEach(v ->  colorInt.add(Integer.parseInt(v)));
                selectedColor = new Color(colorInt.get(0), colorInt.get(1), colorInt.get(2));
            }
            gameofLife.drawing(x,y,selectedColor);
        }
    }
}
