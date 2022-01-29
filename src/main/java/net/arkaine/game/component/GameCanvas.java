package net.arkaine.game.component;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;


import java.awt.*;
import java.awt.image.BufferedImage;

public class GameCanvas extends Canvas {


    private int x = 300;
    private int y = 200;
    private int fading = 1;
    private int scale = 4;
    private BufferedImage buf = new BufferedImage(x,y, BufferedImage.TYPE_INT_RGB);
    private GraphicsContext gc = getGraphicsContext2D();

    public GameCanvas(){
        init();
    }

    public void init() {
        Dimension size
                = Toolkit.getDefaultToolkit().getScreenSize();
        gc = getGraphicsContext2D();
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
        buf = SwingFXUtils.fromFXImage(snp, buf);
        while(buf.getWidth() < 1){
            System.out.print(buf.getRGB(1,1) + " ");
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
                px.setColor( xx, yy, javafx.scene.paint.Color.WHITE);
        SnapshotParameters params = new SnapshotParameters();
        WritableImage snp = this.snapshot(params, null);
        buf = SwingFXUtils.fromFXImage(snp, null);
        setScaleX(scale);
        setScaleY(scale);
    }

    private double randomDouble(){
        return Math.random()*(0.999999);
    }
    private int randomInt(){
        return (int) (Math.random()*(254));
    }

    private javafx.scene.paint.Color getRandomColor() {
        int percent = (int) Math.round(Math.random()*(6));
        javafx.scene.paint.Color value = new javafx.scene.paint.Color(1,1,1,1);
        if(percent == 0 )
            value = new javafx.scene.paint.Color((Math.random()*(1)),(Math.random()*(1)),(Math.random()*(1)),1.0);
        return value;
    }

    public void updateGame() throws Exception {
        boolean isChange = false;

        setScaleX(1);
        setScaleY(1);

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

                java.awt.Color actualCelluleColor = getColor(xx, yy);

                boolean cellule = actualCelluleColor.getRGB() != java.awt.Color.WHITE.getRGB();
                neighbours = getNeighbourValue(buf, neighbours, cellule, actualCelluleColor, xm1, ym1, xx, yy);
                neighbours = getNeighbourValue(buf, neighbours, cellule, actualCelluleColor, xx, ym1, xx, yy);
                neighbours = getNeighbourValue(buf, neighbours, cellule, actualCelluleColor, xp1, ym1, xx, yy);

                neighbours = getNeighbourValue(buf, neighbours, cellule, actualCelluleColor, xm1, yy, xx, yy);
                neighbours = getNeighbourValue(buf, neighbours, cellule, actualCelluleColor, xp1, yy, xx, yy);

                neighbours = getNeighbourValue(buf, neighbours, cellule, actualCelluleColor, xm1, yp1, xx, yy);
                neighbours = getNeighbourValue(buf, neighbours, cellule, actualCelluleColor, xx, yp1, xx, yy);
                neighbours = getNeighbourValue(buf, neighbours, cellule, actualCelluleColor, xp1, yp1, xx, yy);
              //  actualCelluleColor = getColor(xx, yy);
             //   cellule = (actualCelluleColor.getRGB() != Color.WHITE.getRGB());

                if(cellule && neighbours > 4 ){// upgrade color
                    buf.setRGB( xx, yy, incColorWithoutWhite(actualCelluleColor));
                }

                if(cellule &&
                        !isChange ) {
                    // explosion
                    int stressExplosion = 7;
                    if (neighbours == stressExplosion) {
                        buf.setRGB(xm1, ym1, Color.RED.getRGB());
                        buf.setRGB(xx, ym1, Color.RED.getRGB());
                        buf.setRGB(xp1, ym1, Color.RED.getRGB());

                       buf.setRGB(xm1, yy, Color.RED.getRGB());
                       buf.setRGB(xp1, yy, Color.RED.getRGB());

                       buf.setRGB(xm1, yp1, Color.RED.getRGB());
                       buf.setRGB(xx, yp1, Color.RED.getRGB());
                       buf.setRGB(xp1, yp1, Color.RED.getRGB());

                       buf.setRGB(xx, yy, Color.WHITE.getRGB());
                        actualCelluleColor = Color.WHITE;
                        cellule = false;
                        isChange = true;
                    }
                }
                if( !isChange && neighbours == 3){ // create cellule
                    if(!cellule){
                        Color colorStatusTmp = new Color(randomInt(), randomInt(), randomInt());//startingColor[2];
                        cellule = (colorStatusTmp.getRGB() != Color.WHITE.getRGB());
                        buf.setRGB( xx, yy, colorStatusTmp.getRGB());
                    }
                }else{
                    if(cellule
                            && !isChange
                            && (neighbours <2 || neighbours >3)){
                        buf.setRGB( xx, yy, Color.WHITE.getRGB());
                        cellule = false;
                        isChange = true;
                    }
                }

                isChange = false;
//vieillissement
                if(cellule){
                    int[] turpleColor = getRGBCode(xx, yy);
                    if(turpleColor[0] > 0)
                        turpleColor[0] -= fading;
                    if(turpleColor[0] < 0)
                        turpleColor[0] = 0 ;
                    if(turpleColor[0] == 0){
                        if(turpleColor[1] > 0)
                            turpleColor[1] -= fading;
                        if(turpleColor[1] < 0)
                            turpleColor[1] = 0;
                        if(turpleColor[1] == 0){
                            if(turpleColor[2] > 0)
                                turpleColor[2] -= fading;
                            if(turpleColor[2] < 0)
                                turpleColor[2] = 0;
                        }
                    }
                    buf.setRGB( xx, yy, new java.awt.Color(turpleColor[0], turpleColor[1], turpleColor[2]).getRGB());
                }
            }
        }
        if (buf == null) {
            throw new Exception("no buffered image)");
        }

        gc.drawImage( SwingFXUtils.toFXImage(buf, null),0,0);

        setScaleX(scale);
        setScaleY(scale);
    }

    public void drawing(int x, int y, Color color){
        buf.setRGB(x,y,color.getRGB());
        gc.drawImage( SwingFXUtils.toFXImage(buf, null),0,0);
    }

    private java.awt.Color getColor(int xx, int yy) {
        java.awt.Color actualCelluleColor = new java.awt.Color(buf.getRGB(xx, yy));
        return actualCelluleColor;
    }


    private int[] getRGBCode(int xx, int yy) {
        int clr = buf.getRGB(xx, yy);
        return new int[]{(clr & 0x00ff0000) >> 16, (clr & 0x0000ff00) >> 8, clr & 0x000000ff};
    }
    private int[] getRGBCode(java.awt.Color color) {
        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

    private int incColorWithoutWhite(java.awt.Color color){
        int[] turpleColor = getRGBCode(color);
        if(turpleColor[0] <255-fading)
            turpleColor[0] +=fading;
        if(turpleColor[0] > 255-fading)
            turpleColor[0] = 255-fading;
        if(turpleColor[0] == 255-fading){
            if(turpleColor[1]  <255-fading)
                turpleColor[1] +=fading;
            if(turpleColor[1] > 255-fading)
                turpleColor[1] = 255-fading;
            if(turpleColor[1] == 255-fading){
                if(turpleColor[2]  <254-fading)
                    turpleColor[2] +=fading;
                if(turpleColor[2] > 254-fading)
                    turpleColor[2] = 254-fading;
            }
        }
        return new java.awt.Color(turpleColor[0], turpleColor[1], turpleColor[2]).getRGB();
    }

    private int getNeighbourValue(BufferedImage buf, int neighbours, boolean cellule, java.awt.Color actualCelluleColor, int xm, int ym, int xx, int yy) {

        java.awt.Color color = getColor(xm, ym);

//classic
//        if(color.getRGB() != Color.white.getRGB() )
//            neighbours++;
        //burn paper
            if(actualCelluleColor.getRGB() == Color.BLACK.getRGB()){
                int xm1 = xx-1;
                if(xm1 < 0 )
                    xm1 = x-1;
                int xp1 = xx+1;
                if(xp1 >= x)
                    xp1 = 0;
                java.awt.Color colorTest = getColor(xp1, yy);
                if(colorTest != java.awt.Color.WHITE)
                    neighbours++;
                buf.setRGB(xp1, yy, getRandomNewColor().getRGB());
                colorTest = getColor(xm1, yy);
                if(colorTest.getRGB() != java.awt.Color.WHITE.getRGB())
                    neighbours++;
                buf.setRGB(xm1, yy, getRandomNewColor().getRGB());
                buf.setRGB(xx, yy, java.awt.Color.WHITE.getRGB());
            }else
                if(color.getRGB() != Color.WHITE.getRGB())
                    neighbours++;
        return neighbours;
    }

    private java.awt.Color getRandomNewColor() {
        return new java.awt.Color((int)(Math.random()*127), (int)(Math.random()*127), (int)(Math.random()*254));
    }
}

