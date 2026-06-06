package com.chikage.stickermorphbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

public interface UpdateHandler {


    boolean support(Update update);

    void handle(Update update, TelegramBot telegramBot);
}
