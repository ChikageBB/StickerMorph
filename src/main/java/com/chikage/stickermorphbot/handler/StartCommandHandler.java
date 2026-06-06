package com.chikage.stickermorphbot.handler;

import com.chikage.stickermorphbot.command.TelegramCommand;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommandHandler implements UpdateHandler {

    @Override
    public boolean support(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }

        return TelegramCommand.START.isEnabled() &&
                TelegramCommand.START == TelegramCommand.fromValue(update.message().text());
    }

    @Override
    public void handle(Update update, TelegramBot telegramBot) {
        Long chatId = update.message().chat().id();

        log.atInfo()
                .setMessage("user.command.start")
                .addKeyValue("chat_id", update.message().from().id())
                .log();

        telegramBot.execute(new SendMessage(chatId, "Привет👋"));
    }
}
