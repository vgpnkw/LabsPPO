package com.example.mylab3;

import java.util.UUID;

public class GameStructure {
    public static String myName;
    public static String myEmail;
    public static String myPassword;
    public static String myImage;
    public static String myWins;
    public static String myGames;
    public static String opponentName;
    public static String Id;
    public static String Role;
    public static String OpponentRole;
    public static String Action;
    public static String opponentAction;
    public static String opponentImage;


    GameStructure() {

    }

    public GameStructure(String name, String email, String password, String image, String wins, String games) {
        myName = name;
        myEmail = email;
        myPassword = password;
        myImage = image;
        myWins = wins;
        myGames = games;
    }

    public static void newId(){

    }
}
