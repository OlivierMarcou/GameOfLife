package net.arkaine.game.component;

import javafx.scene.paint.Color;

import java.util.EnumSet;

public enum ColorStatus {


    WHITE(0), BLUE(1),
    PURPLE(2), GREEN(3), YELLOW(4), ORANGE(5) ,RED(6) , BLACK(7)
    ;
    private int value;

    ColorStatus(int value) {
        this.value = value;
    }

    public static ColorStatus getColorStatusByValue(int _status) {
        for(ColorStatus colorSt: EnumSet.allOf(ColorStatus.class)){
            if(colorSt.value == _status)
                return colorSt;
        }
        return null;
    }

    public int getValue() {
        return this.value;
    }

    public Color getColor() {
        switch (value){
            case 0 :
                return Color.WHITE;
            case 1 :
                return Color.BLUE;
            case 2 :
                return Color.PURPLE;
            case 3 :
                return Color.GREEN;
            case 4 :
                return Color.YELLOW;
            case 5 :
                return Color.ORANGE;
            case 6 :
                return Color.RED;
            case 7 :
                return Color.BLACK;
        }
        return Color.WHITE;
    }


    public static int getColorValue(Color color) {
        if(color.hashCode() == Color.WHITE.hashCode())
            return 0;
        if(color.hashCode() == Color.BLUE.hashCode())
            return 1;
        if(color.hashCode() == Color.PURPLE.hashCode())
            return 2;
        if(color.hashCode() == Color.GREEN.hashCode())
            return 3;
        if(color.hashCode() == Color.YELLOW.hashCode())
            return 4;
        if(color.hashCode() == Color.ORANGE.hashCode())
            return 5;
        if(color.hashCode() == Color.RED.hashCode())
            return 6;
        if(color.hashCode() == Color.BLACK.hashCode())
            return 7;

        return 0;
    }

    public static int getSize(){
        EnumSet<ColorStatus> cards = EnumSet.allOf(ColorStatus.class) ;
        return cards.size();
    }
}
