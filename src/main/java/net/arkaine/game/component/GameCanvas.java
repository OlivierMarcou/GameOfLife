package net.arkaine.game.component;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class GameCanvas extends Canvas {


    private int x = 800;
    private int y = 400;
    private double fading = 0.005;
    private int scale =2;


    private GraphicsContext gc = getGraphicsContext2D();

    public GameCanvas(){
        init();
    }

    public void init() {
        Dimension size
                = Toolkit.getDefaultToolkit().getScreenSize();
        x = size.width/scale;
        y = size.height/scale;
        setHeight(y);
        setWidth(x);
        PixelWriter px = gc.getPixelWriter();
        for(int xx= 0 ; xx<x ; xx++)
            for(int yy= 0 ; yy<y ; yy++){
                if(xx>0 && xx<x)
                    px.setColor( xx, yy, getRandomColor());
            }
        SnapshotParameters params = new SnapshotParameters();
        WritableImage snp = this.snapshot(params, null);
        buf = SwingFXUtils.fromFXImage(snp, null);
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
        SnapshotParameters params = new SnapshotParameters();
        WritableImage snp = this.snapshot(params, null);
        buf = SwingFXUtils.fromFXImage(snp, null);
        setScaleX(scale);
        setScaleY(scale);
    }

    private double randomDouble(){
        return Math.random()*(0.999999);
    }
    private Color getRandomColor() {
        int percent = (int) Math.round(Math.random()*(6));
        Color value = new Color(1,1,1,1);
        if(percent == 0 )
            value = new Color((Math.random()*(1)),(Math.random()*(1)),(Math.random()*(1)),1.0);
        return value;
    }
    private BufferedImage buf = null;
    public void updateGame() throws Exception {
        boolean isChange = false;

        setScaleX(1);
        setScaleY(1);
        SnapshotParameters params = new SnapshotParameters();
        WritableImage snp = this.snapshot(params, null);
         buf = SwingFXUtils.fromFXImage(snp, null);

        for(int yy= 0 ; yy<y ; yy++){
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
                cellule = (!(turpleColor[0] ==1.0f && turpleColor[1] == 1.0 && turpleColor[2] == 1.0 )? true: false);

                 ColorModel colorModel;
                 final WritableRaster raster;
                if(cellule && neighbours > 4 ){// upgrade color
                    buf.setRGB( xx, yy, getRGBint(incColorWithoutWhite(turpleColor)));
                }

                if(cellule &&
                        !isChange ) {
                    // explosion
                    int stressExplosion = 8;
                    if (neighbours == stressExplosion) {
                        buf.setRGB(xm1, ym1, java.awt.Color.RED.getRGB());
                        buf.setRGB(xx, ym1, java.awt.Color.RED.getRGB());
                        buf.setRGB(xp1, ym1, java.awt.Color.RED.getRGB());

                       buf.setRGB(xm1, yy, java.awt.Color.RED.getRGB());
                       buf.setRGB(xp1, yy, java.awt.Color.RED.getRGB());

                       buf.setRGB(xm1, yp1, java.awt.Color.RED.getRGB());
                       buf.setRGB(xx, yp1, java.awt.Color.RED.getRGB());
                       buf.setRGB(xp1, yp1, java.awt.Color.RED.getRGB());
                       buf.setRGB(xx, yy, java.awt.Color.WHITE.getRGB());
                        turpleColor[0] = 1.0;
                        turpleColor[1] = 1.0;
                        turpleColor[2] = 1.0;
                        cellule = false;
                        isChange = true;
                    }
                }
                if( !isChange && neighbours == 3){ // create cellule
                    if(!cellule){
                        turpleColor[0] = randomDouble();//startingColor[0];
                        turpleColor[1] = randomDouble();//startingColor[1];
                        turpleColor[2] = randomDouble();//startingColor[2];
                        cellule = (!(turpleColor[0] ==1.0 && turpleColor[1] == 1.0 && turpleColor[2] == 1.0 )? true: false);
                        int colorStatusTmp =  getRGBint(turpleColor);
                       buf.setRGB( xx, yy, colorStatusTmp);
                    }
                }else{
                    if(cellule
                            && !isChange
                            && (neighbours <2 || neighbours >3)){

                        turpleColor[0] = 1.0;
                        turpleColor[1] = 1.0;
                        turpleColor[2] = 1.0;
                        int colorStatusTmp =  getRGBint(turpleColor);
                       buf.setRGB( xx, yy,  colorStatusTmp);
                        cellule = false;
                        isChange = true;
                    }
                }

                isChange = false;
//vieillissement
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
                }
                int colorStatusTmp =  getRGBint(turpleColor);
                    buf.setRGB( xx, yy, colorStatusTmp);
            }
        }
        if (buf == null) {
            throw new Exception("no buffered image)");
        }

        gc.drawImage( SwingFXUtils.toFXImage(buf, null),0,0);

        setScaleX(scale);
        setScaleY(scale);
    }
    private double[] incColorWithoutWhite(double[] turpleColor){
        if(turpleColor[0] <1.0-fading)
            turpleColor[0] +=fading;
        if(turpleColor[0] > 1.0-fading)
            turpleColor[0] = 1.0-fading;
        if(turpleColor[0] == 1.0-fading){
            if(turpleColor[1]  <1.0-fading)
                turpleColor[1] +=fading;
            if(turpleColor[1] > 1.0-fading)
                turpleColor[1] = 1.0-fading;
            if(turpleColor[1] == 1.0-fading){
                if(turpleColor[2]  <1.0-fading)
                    turpleColor[2] +=fading;
             //   if(turpleColor[2] > 1.0-fading)
            }
        }
        return turpleColor;
    }

    private int getNeighbourValue(WritableImage snp, int neighbours, boolean cellule, double[] turpleColor, int xm, int ym, int xx, int yy) {

        Color color = snp.getPixelReader().getColor(xm, ym);

            if(turpleColor[0] == 0.0 &&turpleColor[1] == 0.0 && turpleColor[2] == 0.0){
                int xm1 = xx-1;
                if(xm1 < 0 )
                    xm1 = x-1;
                int xp1 = xx+1;
                if(xp1 >= x)
                    xp1 = 0;
                Color colorTest = snp.getPixelReader().getColor(xp1, yy);
                if(colorTest != Color.WHITE)
                    neighbours++;
                snp.getPixelWriter().setColor(xp1, yy, getRandomNewColor());
                colorTest = snp.getPixelReader().getColor(xm1, yy);
                if(colorTest != Color.WHITE)
                    neighbours++;
                snp.getPixelWriter().setColor(xm1, yy, getRandomNewColor());
                snp.getPixelWriter().setColor(xx, yy, Color.WHITE);
            }else
                if(color.getRed() < 1.0 || color.getGreen() < 1.0 || color.getBlue() < 1.0  )
                    neighbours++;

        return neighbours;
    }

    private Color getRandomNewColor() {
        return new Color((Math.random()*(0.5)),(Math.random()*(0.5)),(Math.random()*(0.999999)),1.0);
    }
    
    private int getRGBint(double[] turpletColor){
        java.awt.Color color = new java.awt.Color((int)( turpletColor[0] * 255),
                (int)( turpletColor[1] * 255),
                (int)( turpletColor[2] * 255));
        return color.getRGB();
    }
}

