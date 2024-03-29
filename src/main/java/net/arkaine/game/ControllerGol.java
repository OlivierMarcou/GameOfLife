package net.arkaine.game;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import net.arkaine.game.component.GameCanvas;
import net.arkaine.game.component.GameMode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerGol {

    private boolean isPlay = false;

    @FXML
    private ImageView pauseImg;

    @FXML
    private GameCanvas gameofLife;

    @FXML
    private ImageView playImg;

    @FXML
    private ImageView drawImg;

    @FXML
    private ImageView stopDrawImg;

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
            long speed = 500;
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
        th.setDaemon(true);
        th.start();
    }

    @FXML
    private void clearAction(ActionEvent event) {
        gameofLife.clear();
        event.consume();
    }

    @FXML
    private Button saveBtn;

    @FXML
    private Button loadBtn;

    @FXML
    private void saveAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);
        if(file != null){
            try {
                gameofLife.setScaleX(1);
                gameofLife.setScaleY(1);
                WritableImage writableImage = new WritableImage(gameofLife.getX(), gameofLife.getY());
                gameofLife.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                Logger.getLogger(ControllerGol.class.getName()).log(Level.SEVERE, null, ex);
            }
            gameofLife.setScaleX(gameofLife.getScale());
            gameofLife.setScaleY(gameofLife.getScale());
        }
    }

    @FXML
    private void loadAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);

        if(file != null){
            gameofLife.setScaleX(1);
            gameofLife.setScaleY(1);
            try {
                BufferedImage buf = ImageIO.read(file);
                gameofLife.getGraphicsContext2D().drawImage( SwingFXUtils.toFXImage(buf, null),0,0);
            } catch (IOException ex) {
                Logger.getLogger(ControllerGol.class.getName()).log(Level.SEVERE, null, ex);
            }
            gameofLife.setScaleX(gameofLife.getScale());
            gameofLife.setScaleY(gameofLife.getScale());
        }
    }

    @FXML
    private Slider speedSld;

    private boolean drawing = true;

    @FXML
    public void drawCanvas(MouseEvent mouseEvent) {
        if(drawing && mouseEvent.isPrimaryButtonDown()) {
            int x = (int)Math.round(mouseEvent.getX());
            int y = (int)Math.round(mouseEvent.getY());
            Color selectedColor = new Color(0.0,0.0,1.0,1.0);
            if(colorComboBox.getSelectionModel().getSelectedItem() != null){
                List<Double> colorDouble = new ArrayList<>();
                Arrays.stream(colorComboBox.getSelectionModel().getSelectedItem().split(","))
                        .forEach(v ->  colorDouble.add(Double.parseDouble(v)));
                selectedColor = new Color(colorDouble.get(0), colorDouble.get(1), colorDouble.get(2),1.0);
            }
            gameofLife.getGraphicsContext2D().getPixelWriter().setColor(x, y, selectedColor);
        }
    }

    @FXML
    private Button drawBtn;
    public void switchDrawAction(ActionEvent actionEvent) {
        drawing = !drawing;
        if(drawing){
            drawBtn.setGraphic(drawImg);
        }else{
            if(!stopDrawImg.isVisible())stopDrawImg.setVisible(true);
            drawBtn.setGraphic(stopDrawImg);
        }
        actionEvent.consume();
    }

    @FXML
    private Button oriBtn;
    public void switchOriAction(ActionEvent actionEvent) {
        int gameMode = gameofLife.getMode().getValue();
        gameMode++;
        gameofLife.setMode(GameMode.getGameMode(gameMode));
            gameofLife.setMaxValue(1.0);

        oriBtn.setText(gameofLife.getMode().name());
    }
}
