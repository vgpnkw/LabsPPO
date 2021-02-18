package com.example.seabattle;

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
    public static String myImageType;
    public static String opponentImageType;
    public static String opponentEmail;


    GameStructure() {

    }

    public GameStructure(String name, String email, String password, String image, String wins, String games) {
        myName = name;
        myEmail = email;
        myPassword = password;
        myImage = image;
        myWins = wins;
        myGames = games;
        myImageType = "";
    }

    public static void newId(){

    }
}

