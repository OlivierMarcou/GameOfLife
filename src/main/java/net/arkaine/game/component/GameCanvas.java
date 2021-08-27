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


    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    private GameMode mode = GameMode.ORIGINAL;
    private int x = 200;
    private int y = 200;


    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    private Double maxValue = 1.0;

    private int scale = 2;
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
            for(int yy= 0 ; yy<y ; yy++){
                if(xx>0 && xx<x)
                    px.setColor( xx, yy, getRandomColor());
            }
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
        return new Color((Math.random()*(1)),(Math.random()*(1)),(Math.random()*(1)),1.0);
    }

    public void updateGame(){
        age++;
        setScaleX(1);
        setScaleY(1);
        SnapshotParameters params = new SnapshotParameters();
        WritableImage snp = this.snapshot(params, null);
        PixelWriter px = gc.getPixelWriter();
        for(int yy= 0 ; yy<y ; yy++)
            for(int xx= 0 ; xx<x ; xx++)
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
                boolean isShot = false;
                Double cellule =  (snp.getPixelReader().getColor(xx, yy).getRed());
                neighbours = getNeighbourValue(snp, neighbours, xm1, ym1,  isShot, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, xx, ym1,  isShot, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, xp1, ym1,  isShot, xx, yy);

                neighbours = getNeighbourValue(snp, neighbours, xm1, yy,  isShot, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, xp1, yy,  isShot, xx, yy);

                neighbours = getNeighbourValue(snp, neighbours, xm1, yp1,  isShot, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, xx, yp1,  isShot, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, xp1, yp1,  isShot, xx, yy);

//                if(neighbours > 5 && mode != GameMode.ORIGINAL && mode != GameMode.COLOR){// upgrade color
//                    if(cellule < maxValue)
//                        if(cellule > 0)
//                            cellule++;
//                    ColorStatus colorStatusTmp = ColorStatus.getColorStatusByValue(cellule);
//                    px.setColor( xx, yy, colorStatusTmp.getColor());
//                }

                if(neighbours == 3){ // create cellule
                    if(mode == GameMode.BURN_PAPER || mode == GameMode.NUKLEAR_BURN){
                        cellule = maxValue;
                    }else{
                        cellule = 1.0;
                    }
                    Color colorStatusTmp = new Color(cellule, cellule,
                            cellule,1.0);
                    px.setColor( xx, yy, colorStatusTmp);
                }

                if(neighbours <2){
                    if(cellule > 0)
                        cellule -=0.001;
                    px.setColor( xx, yy, new Color(cellule, cellule,
                            cellule,1.0));
                }

                if(neighbours <2 || neighbours >3){
                    if(cellule > 0)
                        cellule -=0.001;
                    px.setColor( xx, yy, new Color(cellule, cellule,
                            cellule,1.0));
                }
                // explosion
                int stressExplosion = 8;
                if (mode.equals(GameMode.NUKLEAR_BURN))
                    stressExplosion = 7;
                if(neighbours == stressExplosion && cellule > 0 && !mode.equals(GameMode.ORIGINAL) && !mode.equals(GameMode.EXTEND)){
                    if(mode.equals(GameMode.COLOR))
                        cellule = 0.0 ;
                    else
                        cellule -=0.001;
                    px.setColor(xm1, ym1, Color.RED);
                    px.setColor(xx, ym1, Color.RED);
                    px.setColor(xp1, ym1, Color.RED);

                    px.setColor(xm1, yy, Color.RED);
                    px.setColor(xp1, yy, Color.RED);

                    px.setColor(xm1, yp1, Color.RED);
                    px.setColor(xx, yp1, Color.RED);
                    px.setColor(xp1, yp1, Color.RED);
                    px.setColor( xx, yy, Color.WHITE);
                }
            }
        setScaleX(scale);
        setScaleY(scale);
    }

    private int getNeighbourValue(WritableImage snp, int neighbours, int xm, int ym,  boolean isShot, int xx , int yy) {
        Color color = snp.getPixelReader().getColor(xm, ym);
        int neighbour = ColorStatus.getColorValue(color);

        if(neighbour > 0 )
            neighbours++;
        return neighbours;
    }
}

