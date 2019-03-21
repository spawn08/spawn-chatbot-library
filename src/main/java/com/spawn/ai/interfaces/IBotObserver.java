package com.spawn.ai.interfaces;

import com.spawn.ai.model.BotResponse;

public interface IBotObserver {

    public void notifyBotResponse(BotResponse botResponse);

    public void notifyBotError();

    public void loading();

    public void speakBot(String message);
}
