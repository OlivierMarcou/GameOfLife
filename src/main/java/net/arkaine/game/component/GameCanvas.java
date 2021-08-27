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
    private Double maxValue = 1.0;
    private double fading = 0.01;
    private int scale = 4;

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

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
        int percent = (int) Math.round(Math.random()*(20));
        Color value = new Color(1,1,1,1);
        if(percent == 0 )
            value = new Color((Math.random()*(1)),(Math.random()*(1)),(Math.random()*(1)),1.0);
        return value;
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
                Double celluleR =  (snp.getPixelReader().getColor(xx, yy).getRed());
                Double celluleV =  (snp.getPixelReader().getColor(xx, yy).getGreen());
                Double celluleB =  (snp.getPixelReader().getColor(xx, yy).getBlue());
                neighbours = getNeighbourValue(snp, neighbours, xm1, ym1);
                neighbours = getNeighbourValue(snp, neighbours, xx, ym1);
                neighbours = getNeighbourValue(snp, neighbours, xp1, ym1);

                neighbours = getNeighbourValue(snp, neighbours, xm1, yy);
                neighbours = getNeighbourValue(snp, neighbours, xp1, yy);

                neighbours = getNeighbourValue(snp, neighbours, xm1, yp1);
                neighbours = getNeighbourValue(snp, neighbours, xx, yp1);
                neighbours = getNeighbourValue(snp, neighbours, xp1, yp1);

//                if(neighbours > 5 && mode != GameMode.ORIGINAL && mode != GameMode.COLOR){// upgrade color
//                    if(cellule < maxValue)
//                        if(cellule > 0)
//                            cellule++;
//                    ColorStatus colorStatusTmp = ColorStatus.getColorStatusByValue(cellule);
//                    px.setColor( xx, yy, colorStatusTmp.getColor());
//                }

                boolean cellule = (!(celluleR ==1.0 && celluleV == 1.0 && celluleB == 1.0 )? true: false);
                if(neighbours == 3){ // create cellule
                    if(mode == GameMode.BURN_PAPER || mode == GameMode.NUKLEAR_BURN){
                        if(celluleR > 0)
                            celluleR -=fading;
                        if(celluleR < 0)
                            celluleR =0.0;
                        if(celluleR == 0){
                            if(celluleV > 0)
                                celluleV -=fading;
                            if(celluleV < 0)
                                celluleV =0.0;
                            if(celluleV == 0){
                                if(celluleB > 0)
                                    celluleB -=fading;
                                if(celluleB < 0)
                                    celluleB =0.0;
                            }
                        }
                    }else{
                        celluleR = 0.0;
                        celluleV = 0.0;
                        celluleB = 1.0;
                    }
                    Color colorStatusTmp = new Color(celluleR, celluleV,
                            celluleB, 1.0);
                    px.setColor( xx, yy, colorStatusTmp);
                }

             //   cellule =  (!(celluleR ==1.0 && celluleV == 1.0 && celluleB == 1.0 )? true: false);
                if(neighbours <2 || neighbours >3){
                    if(cellule ){

                        celluleR = 1.0;
                        celluleV = 1.0;
                        celluleB = 1.0;
                        px.setColor( xx, yy, new Color(celluleR, celluleV,
                                celluleB, 1.0));
                    }
                }
                cellule =  (!(celluleR ==1.0 && celluleV == 1.0 && celluleB == 1.0 )? true: false);

                // explosion
                int stressExplosion = 8;
                if (mode.equals(GameMode.NUKLEAR_BURN))
                    stressExplosion = 7;
                if(neighbours == stressExplosion && cellule && !mode.equals(GameMode.ORIGINAL) && !mode.equals(GameMode.EXTEND)){
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

    private int getNeighbourValue(WritableImage snp, int neighbours, int xm, int ym) {
        Color color = snp.getPixelReader().getColor(xm, ym);

        if(color.getRed() < 1.0 || color.getGreen() < 1.0 || color.getBlue() < 1.0  )
            neighbours++;
        return neighbours;
    }
}

