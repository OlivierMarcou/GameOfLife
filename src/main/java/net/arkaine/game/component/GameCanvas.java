package net.arkaine.game.component;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GameCanvas extends Canvas {

    private boolean isOriginal = false;
    private int x = 200;
    private int y = 200;

    private int maxValue = 7;

    private int scale = 4;
    private int age = 0;

    private GraphicsContext gc = getGraphicsContext2D();

    private HashMap<java.awt.Color,Integer> colorsInt = new HashMap<>();
    private HashMap<Integer, java.awt.Color> intColors = new HashMap<>();
    private HashMap<Integer, Color> colors = new HashMap<>();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }


    public GameCanvas(){

        colorsInt.put( java.awt.Color.WHITE,0);
        colorsInt.put( java.awt.Color.BLUE, 1);
        colorsInt.put( new java.awt.Color(-8388480), 2);
        colorsInt.put( new java.awt.Color(-16744448), 3);
        colorsInt.put( java.awt.Color.YELLOW, 4);
        colorsInt.put( new java.awt.Color(-23296), 5);
        colorsInt.put( java.awt.Color.RED, 6);
        colorsInt.put( java.awt.Color.BLACK, 7);

        intColors.put(0, java.awt.Color.WHITE);
        intColors.put(1, java.awt.Color.BLUE);
        intColors.put(2, new java.awt.Color(-8388480));
        intColors.put(3, new java.awt.Color(-16744448));
        intColors.put(4, java.awt.Color.YELLOW);
        intColors.put(5, new java.awt.Color(-23296));
        intColors.put(6, java.awt.Color.RED);
        intColors.put(7, java.awt.Color.BLACK);
        colors.put(0, Color.WHITE);
        colors.put(1, Color.BLUE);
        colors.put(2, Color.PURPLE);
        colors.put(3, Color.GREEN);
        colors.put(4, Color.YELLOW);
        colors.put(5, Color.ORANGE);
        colors.put(6, Color.RED);
        colors.put(7, Color.BLACK);

        init();
    }

    public void init() {
        PixelWriter px = gc.getPixelWriter();
        setHeight(x);
        setWidth(y);
        for(int xx= 0 ; xx<x ; xx++)
            for(int yy= 0 ; yy<y ; yy++)
                px.setColor( xx, yy, getRandomColor());
        initBuff();
    }

    private Image resample(Image input, int scaleFactor) {
        final int W = (int) input.getWidth();
        final int H = (int) input.getHeight();
        final int S = scaleFactor;

        WritableImage output = new WritableImage(
                W * S,
                H * S
        );

        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                final int argb = reader.getArgb(x, y);
                for (int dy = 0; dy < S; dy++) {
                    for (int dx = 0; dx < S; dx++) {
                        writer.setArgb(x * S + dx, y * S + dy, argb);
                    }
                }
            }
        }

        return output;
    }

    private void initBuff() {
        setScaleX(1);
        setScaleY(1);
        WritableImage writableImage = new WritableImage(x, y);
        snapshot(null, writableImage);
        buff = SwingFXUtils.fromFXImage(writableImage, null);
        setScaleX(scale);
        setScaleY(scale);
    }

    public void clear() {
        PixelWriter px = gc.getPixelWriter();
        setHeight(x);
        setWidth(y);
        for(int xx= 0 ; xx<x ; xx++)
            for(int yy= 0 ; yy<y ; yy++)
                px.setColor( xx, yy, Color.WHITE);
        initBuff();
    }

    private Color getRandomColor() {
        int percent = (int) Math.round(Math.random()*(6));
        int value = 0;
        if(percent == 0 )
            value = (int) Math.round(Math.random()*(maxValue));
        return colors.get(value);
    }

    private BufferedImage buff = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
    public void updateGame(){
        age++;
        for(int xx= 0 ; xx<x ; xx++)
            for(int yy= 0 ; yy<y ; yy++)
            {
                Integer neighbours = 0;
                int xm1 = xx-1;
                if(xm1 < 0 )
                    xm1 = x-1;

                int xp1 = xx+1;
                if(xp1 >= x)
                    xp1 = 0;

                int ym1 = yy-1;
                if(ym1 < 0 )
                    ym1 = y-1;

                int yp1 = yy+1;
                if(yp1 >= y)
                    yp1 = 0;

                neighbours = getNeighbourValue( neighbours, xm1, ym1);
                neighbours = getNeighbourValue( neighbours, xx, ym1);
                neighbours = getNeighbourValue( neighbours, xp1, ym1);

                neighbours = getNeighbourValue( neighbours, xm1, yy);
                neighbours = getNeighbourValue( neighbours, xp1, yy);

                neighbours = getNeighbourValue( neighbours, xm1, yp1);
                neighbours = getNeighbourValue( neighbours, xx, yp1);
                neighbours = getNeighbourValue( neighbours, xp1, yp1);

                int cellule = colorsInt.get (new java.awt.Color(buff.getRGB(xx,yy)));

//                if(neighbours == 8 && !isOriginal){
//                        cellule = 1;
//                    buff.setRGB(xx, yy, java.awt.Color.BLUE.getRGB());
//
//                    buff.setRGB(xm1, ym1, java.awt.Color.RED.getRGB());
//                    buff.setRGB(xx, ym1, java.awt.Color.RED.getRGB());
//                    buff.setRGB(xp1, ym1, java.awt.Color.RED.getRGB());
//
//                    buff.setRGB(xm1, yy, java.awt.Color.RED.getRGB());
//                    buff.setRGB(xp1, yy, java.awt.Color.RED.getRGB());
//
//                    buff.setRGB(xm1, yp1, java.awt.Color.RED.getRGB());
//                    buff.setRGB(xx, yp1, java.awt.Color.RED.getRGB());
//                    buff.setRGB(xp1, yp1, java.awt.Color.RED.getRGB());
//                }

                if(neighbours == 3){
                    if(cellule < maxValue)
                        if(neighbours>3 && cellule > 0)
                            cellule++;
                        else
                            cellule = 1;
                    buff.setRGB( xx, yy,  intColors.get(cellule).getRGB());
                }

                if(neighbours <2){
                    if(cellule > 0)
                        cellule--;
                    buff.setRGB( xx, yy,  intColors.get(cellule).getRGB());
                }

                if(neighbours <2 || neighbours >3){
                    if(cellule > 0)
                        cellule--;
                    buff.setRGB( xx, yy, intColors.get(cellule).getRGB());
                }
            }
        setScaleX(1);
        setScaleY(1);
        getGraphicsContext2D().drawImage( SwingFXUtils.toFXImage(buff, null),0,0);
        setScaleX(scale);
        setScaleY(scale);
    }

    private int getNeighbourValue(int neighbours, int xxx, int yyy) {
        int neighbour = buff.getRGB(xxx, yyy);
        if(neighbour != -1 )
            neighbours++;
        return neighbours;
    }
}

