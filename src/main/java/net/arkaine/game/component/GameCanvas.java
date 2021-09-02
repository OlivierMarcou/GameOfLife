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
    private double[] startingColor ={ 1.0,0.5,0.5};
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
        int percent = (int) Math.round(Math.random()*(6));
        Color value = new Color(1,1,1,1);
        if(percent == 0 )
            value = new Color((Math.random()*(1)),(Math.random()*(1)),(Math.random()*(1)),1.0);
        return value;
    }

    public void updateGame(){
        boolean isChange = false;
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

                Color color = snp.getPixelReader().getColor(xx, yy);
                double [] turpleColor = {color.getRed(), color.getGreen(), color.getBlue()};

                boolean cellule = (!(turpleColor[0] ==1.0 && turpleColor[1] == 1.0 && turpleColor[2] == 1.0 )? true: false);
                neighbours = getNeighbourValue(snp, neighbours, cellule, turpleColor, xm1, ym1, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, cellule, turpleColor, xx, ym1, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, cellule, turpleColor, xp1, ym1, xx, yy);

                neighbours = getNeighbourValue(snp, neighbours, cellule, turpleColor, xm1, yy, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, cellule, turpleColor, xp1, yy, xx, yy);

                neighbours = getNeighbourValue(snp, neighbours, cellule, turpleColor, xm1, yp1, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, cellule, turpleColor, xx, yp1, xx, yy);
                neighbours = getNeighbourValue(snp, neighbours, cellule, turpleColor, xp1, yp1, xx, yy);

//                if(neighbours > 5 && mode != GameMode.ORIGINAL && mode != GameMode.COLOR){// upgrade color
//                    if(cellule < maxValue)
//                        if(cellule > 0)
//                            cellule++;
//                    ColorStatus colorStatusTmp = ColorStatus.getColorStatusByValue(cellule);
//                    px.setColor( xx, yy, colorStatusTmp.getColor());
//                }

                cellule = (!(turpleColor[0] ==1.0 && turpleColor[1] == 1.0 && turpleColor[2] == 1.0 )? true: false);
                if(neighbours == 3){ // create cellule
                        if(!cellule){
                            turpleColor[0] = startingColor[0];
                            turpleColor[1] = startingColor[1];
                            turpleColor[2] = startingColor[2];
                        }
                        if(turpleColor[0] > 0)
                            turpleColor[0] -=fading;
                        if(turpleColor[0] < 0)
                            turpleColor[0] =0.0;
                        if(turpleColor[0] == 0){
                            if(turpleColor[1] > 0)
                                turpleColor[1] -=fading;
                            if(turpleColor[1] < 0)
                                turpleColor[1] =0.0;
                            if(turpleColor[1] == 0){
                                if(turpleColor[2] > 0)
                                    turpleColor[2] -=fading;
                                if(turpleColor[2] < 0)
                                    turpleColor[2] =0.0;
                            }
                        }
                  //  }
                    Color colorStatusTmp = new Color(turpleColor[0], turpleColor[1],
                            turpleColor[2], 1.0);
                    px.setColor( xx, yy, colorStatusTmp);
                    isChange = true;
                }

                if(!isChange && cellule && !mode.equals(GameMode.ORIGINAL) && !mode.equals(GameMode.EXTEND)) {
                    // explosion
                    int stressExplosion = 8;
                    if (mode.equals(GameMode.NUKLEAR_BURN))
                        stressExplosion = 7;
                    if (neighbours == stressExplosion) {
                        px.setColor(xm1, ym1, Color.RED);
                        px.setColor(xx, ym1, Color.RED);
                        px.setColor(xp1, ym1, Color.RED);

                        px.setColor(xm1, yy, Color.RED);
                        px.setColor(xp1, yy, Color.RED);

                        px.setColor(xm1, yp1, Color.RED);
                        px.setColor(xx, yp1, Color.RED);
                        px.setColor(xp1, yp1, Color.RED);
                        px.setColor(xx, yy, Color.WHITE);
                        turpleColor[0] = 1.0;
                        turpleColor[1] = 1.0;
                        turpleColor[2] = 1.0;
                        isChange = true;
                    }
                }

                //dead cellule
                cellule =  (!(turpleColor[0] ==1.0 && turpleColor[1] == 1.0 && turpleColor[2] == 1.0 )? true: false);
                if(cellule && !isChange && (neighbours <2 || neighbours >3)){

                        turpleColor[0] = 1.0;
                        turpleColor[1] = 1.0;
                        turpleColor[2] = 1.0;
                        px.setColor( xx, yy, new Color(turpleColor[0], turpleColor[1],
                                turpleColor[2], 1.0));
                        isChange = true;

                }
//vieillissement
                cellule =  (!(turpleColor[0] ==1.0 && turpleColor[1] == 1.0 && turpleColor[2] == 1.0 )? true: false);
                if(cellule){
                    if(turpleColor[0] > 0)
                        turpleColor[0] -=fading;
                    if(turpleColor[0] < 0)
                        turpleColor[0] =0.0;
                    if(turpleColor[0] == 0){
                        if(turpleColor[1] > 0)
                            turpleColor[1] -=fading;
                        if(turpleColor[1] < 0)
                            turpleColor[1] =0.0;
                        if(turpleColor[1] == 0){
                            if(turpleColor[2] > 0)
                                turpleColor[2] -=fading;
                            if(turpleColor[2] < 0)
                                turpleColor[2] =0.0;
                        }
                    }

                    px.setColor( xx, yy, new Color(turpleColor[0], turpleColor[1],
                            turpleColor[2], 1.0));
                }
            }
        setScaleX(scale);
        setScaleY(scale);
    }

    private Color decreasColor(double[] turpleColor){
        if(turpleColor[0] > 0)
            turpleColor[0] -=fading;
        if(turpleColor[0] < 0)
            turpleColor[0] =0.0;
        if(turpleColor[0] == 0){
            if(turpleColor[1] > 0)
                turpleColor[1] -=fading;
            if(turpleColor[1] < 0)
                turpleColor[1] =0.0;
            if(turpleColor[1] == 0){
                if(turpleColor[2] > 0)
                    turpleColor[2] -=fading;
                if(turpleColor[2] < 0)
                    turpleColor[2] =0.0;
            }
        }
        return  new Color(turpleColor[0], turpleColor[1], turpleColor[2], 1.0);
    }
    private Color incColor(double[] turpleColor){
        if(turpleColor[0] <1.0)
            turpleColor[0] +=fading;
        if(turpleColor[0] > 1.0)
            turpleColor[0] = 1.0;
        if(turpleColor[0] == 1.0){
            if(turpleColor[1]  <1.0)
                turpleColor[1] +=fading;
            if(turpleColor[1] > 1.0)
                turpleColor[1] = 1.0;
            if(turpleColor[1] == 1.0){
                if(turpleColor[2]  <1.0)
                    turpleColor[2] +=fading;
                if(turpleColor[2] > 1.0)
                    turpleColor[2] = 1.0;
            }
        }
        return  new Color(turpleColor[0], turpleColor[1], turpleColor[2], 1.0);
    }


    private Color incColorWithoutWhite(double[] turpleColor){
        if(turpleColor[0] <1.0)
            turpleColor[0] +=fading;
        if(turpleColor[0] > 1.0)
            turpleColor[0] = 1.0;
        if(turpleColor[0] == 1.0){
            if(turpleColor[1]  <1.0)
                turpleColor[1] +=fading;
            if(turpleColor[1] > 1.0)
                turpleColor[1] = 1.0;
            if(turpleColor[1] == 1.0){
                if(turpleColor[2]  <1.0-fading)
                    turpleColor[2] +=fading;
                if(turpleColor[2] > 1.0-fading)
                    return  new Color(turpleColor[0], turpleColor[1], turpleColor[2], 1.0);
            }
        }
        return  new Color(turpleColor[0], turpleColor[1], turpleColor[2], 1.0);
    }
    private int getNeighbourValue(WritableImage snp, int neighbours, boolean cellule, double[] turpleColor, int xm, int ym, int xx, int yy) {

        Color color = snp.getPixelReader().getColor(xm, ym);
        if(mode == GameMode.EXTEND){
            double [] neighbourColor = {color.getRed(), color.getGreen(), color.getBlue()};
            boolean neighbourCellule =  (!(neighbourColor[0] ==1.0 && neighbourColor[1] == 1.0 && neighbourColor[2] == 1.0 )? true: false);
            if(cellule){
                if(turpleColor[0] >= 0.6 && turpleColor[0] < 1.0){
                    if(neighbours > 2)
                    {
                        snp.getPixelWriter().setColor(xx, yy, decreasColor(turpleColor));
                    }
                }else
                if(neighbourCellule && turpleColor[0] >= 0.3 && turpleColor[0] < 0.6){
                            snp.getPixelWriter().setColor(xm, ym, incColorWithoutWhite(turpleColor));
                            snp.getPixelWriter().setColor(xx, yy, Color.GREEN);

                }else
                if(neighbourCellule && turpleColor[0] >= 0.0 && turpleColor[0] < 0.3){
                        neighbourCellule = false;
                        snp.getPixelWriter().setColor(xm, ym, incColorWithoutWhite(turpleColor));
                        snp.getPixelWriter().setColor(xx, yy, Color.YELLOW);

                }else
                if(neighbourCellule && turpleColor[1] >= 0.6 && turpleColor[1] < 1.0){
                        neighbourCellule = true;
                        snp.getPixelWriter().setColor(xm, ym, decreasColor(turpleColor));

                }else
                if(neighbourCellule && turpleColor[1] >= 0.3 && turpleColor[1] < 0.6){
                        neighbourCellule = true;
                        snp.getPixelWriter().setColor(xm, ym, decreasColor(turpleColor));
                        snp.getPixelWriter().setColor(xx, yy, Color.BLACK);

                }else
                if(neighbourCellule && turpleColor[1] >= 0.0 && turpleColor[1] < 0.3){
                        neighbourCellule = true;
                        snp.getPixelWriter().setColor(xm, ym, Color.BLACK);
                        snp.getPixelWriter().setColor(xx, yy,  new Color((Math.random()*(1)),(Math.random()*(1)),(Math.random()*(1)),0.99999));

                }
            }
            if(neighbourCellule)
                neighbours++;
        }else {
            if(color.getRed() < 1.0 || color.getGreen() < 1.0 || color.getBlue() < 1.0  )
                neighbours++;
        }
        return neighbours;
    }
}

