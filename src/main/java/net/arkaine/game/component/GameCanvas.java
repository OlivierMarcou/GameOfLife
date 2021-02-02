package net.arkaine.game.component;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GameCanvas extends Canvas {

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

    private boolean isOriginal = false;
    private int x = 200;
    private int y = 200;

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

    private int maxValue = 7;

    private int scale = 4;
    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    private int age = 0;

    private GraphicsContext gc = getGraphicsContext2D();

    public GameCanvas(){
        init();
    }

    public void init() {
        PixelWriter px = gc.getPixelWriter();
        setHeight(x);
        setWidth(y);
        for(int xx= 0 ; xx<x ; xx++)
            for(int yy= 0 ; yy<y ; yy++)
                px.setColor( xx, yy, getRandomColor());
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
        setScaleX(scale);
        setScaleY(scale);
    }

    private Color getRandomColor() {
        int percent = (int) Math.round(Math.random()*(6));
        int value = 0;
        if(percent == 0 )
            value = (int) Math.round(Math.random()*(maxValue));
        return ColorStatus.getColorStatusByValue(value).getColor();
    }

    public void updateGame(){
        age++;
        setScaleX(1);
        setScaleY(1);
        SnapshotParameters params = new SnapshotParameters();
        WritableImage snp = this.snapshot(params, null);
        PixelWriter px = gc.getPixelWriter();
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

                neighbours = getNeighbourValue(snp, neighbours, xm1, ym1);
                neighbours = getNeighbourValue(snp, neighbours, xx, ym1);
                neighbours = getNeighbourValue(snp, neighbours, xp1, ym1);

                neighbours = getNeighbourValue(snp, neighbours, xm1, yy);
                neighbours = getNeighbourValue(snp, neighbours, xp1, yy);

                neighbours = getNeighbourValue(snp, neighbours, xm1, yp1);
                neighbours = getNeighbourValue(snp, neighbours, xx, yp1);
                neighbours = getNeighbourValue(snp, neighbours, xp1, yp1);

                int cellule =  ColorStatus.getColorValue(snp.getPixelReader().getColor(xx, yy));

                if(neighbours == 8 && !isOriginal){
                        cellule = 1;
                    ColorStatus colorStatusTmp = ColorStatus.getColorStatusByValue(cellule);
                    px.setColor( xx, yy, colorStatusTmp.getColor());
                    px.setColor(xm1, ym1, Color.RED);
                    px.setColor(xx, ym1, Color.RED);
                    px.setColor(xp1, ym1, Color.RED);

                    px.setColor(xm1, yy, Color.RED);
                    px.setColor(xp1, yy, Color.RED);

                    px.setColor(xm1, yp1, Color.RED);
                    px.setColor(xx, yp1, Color.RED);
                    px.setColor(xp1, yp1, Color.RED);
                    px.setColor( xx, yy, colorStatusTmp.getColor());
                }

                if(neighbours == 3){
                    if(cellule < maxValue)
                        if(neighbours>3 && cellule > 0)
                            cellule++;
                        else
                            cellule = 1;
                    ColorStatus colorStatusTmp = ColorStatus.getColorStatusByValue(cellule);
                    px.setColor( xx, yy, colorStatusTmp.getColor());
                }

                if(neighbours <2){
                    if(cellule > 0)
                        cellule--;
                    ColorStatus colorStatusTmp = ColorStatus.getColorStatusByValue(cellule);
                    px.setColor( xx, yy, colorStatusTmp.getColor());
                }

                if(neighbours <2 || neighbours >3){
                    if(cellule > 0)
                        cellule--;
                    ColorStatus colorStatusTmp = ColorStatus.getColorStatusByValue(cellule);
                    px.setColor( xx, yy, colorStatusTmp.getColor());
                }
            }
        setScaleX(scale);
        setScaleY(scale);
    }

    private int getNeighbourValue(WritableImage snp, int neighbours, int xm1, int ym1) {
        Color color = snp.getPixelReader().getColor(xm1, ym1);
        int neighbour = ColorStatus.getColorValue(color);
        if(neighbour > 0 )
            neighbours++;
        return neighbours;
    }
}

