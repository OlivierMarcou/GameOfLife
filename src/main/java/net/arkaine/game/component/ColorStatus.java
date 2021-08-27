package net.arkaine.game.component;

import javafx.scene.paint.Color;

import java.util.EnumSet;

public class ColorStatus {


    private int value;

    ColorStatus(int value) {
        this.value = value;
    }


    public int getValue() {
        return this.value;
    }


    public static int getColorValue(Color color) {
        return  (int)color.getRed();
    }

}
