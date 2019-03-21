package com.spawn.ai.model;

import java.util.ArrayList;

public class Entities {

    private ArrayList<Intent> intent;

    public ArrayList<Intent> getBotIntents() {
        return intent;
    }

    public void setBotIntents(ArrayList<Intent> botIntents) {
        this.intent = botIntents;
    }
}
