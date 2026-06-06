package com.chikage.stickermorphbot.handler;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UnknownCommandHandler implements UpdateHandler {


    @Override
    public boolean support(Update update) {
        return update.message() != null && update.message().text() != null;
    }

    @Override
    public void handle(Update update, TelegramBot telegramBot) {
        Long chatId = update.message().chat().id();
        log.warn(
                "Пользователь {} вызвал неизвестную команду: {}",
                update.message().from().id(),
                update.message().text());
        telegramBot.execute(new SendMessage(
                chatId, "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд"));
    }
}
