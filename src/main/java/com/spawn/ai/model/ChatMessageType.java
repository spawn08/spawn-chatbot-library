package com.spawn.ai.model;

public class ChatMessageType {

    private String message;
    private int viewType; //0=User chat, 1= Bot Chat
    private BotResponse botResponse;
    private String date;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public BotResponse getBotResponse() {
        return botResponse;
    }

    public void setBotResponse(BotResponse botResponse) {
        this.botResponse = botResponse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
