package net.arkaine.game;

import javafx.application.Application;

public class Main  {

    private Main()
    {}

    public static void main(String[] args) {
        Application.launch(GameofLifeApplication.class, args);
    }

    private static void sp(byte[] decode) {
        System.out.println(new String(decode));
    }

}