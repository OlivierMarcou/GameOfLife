package net.arkaine.game.component;

import javafx.scene.paint.Color;

import java.util.EnumSet;

public enum GameMode {

    ORIGINAL(1), BURN_PAPER (2), COLOR (3) , EXTEND ( 4), NUKLEAR_BURN (5) ;
    private int value;

    GameMode(int value) {
        this.value = value;
    }

    public static GameMode getGameMode(int _status) {
        for(GameMode colorSt: EnumSet.allOf(GameMode.class)){
            if(colorSt.value == _status)
                return colorSt;
        }
        return GameMode.ORIGINAL;
    }

    public int getValue() {
        return this.value;
    }

}
