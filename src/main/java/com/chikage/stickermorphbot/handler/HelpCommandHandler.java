package com.chikage.stickermorphbot.handler;

import com.chikage.stickermorphbot.command.TelegramCommand;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HelpCommandHandler implements UpdateHandler{


    @Override
    public boolean support(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }

        return TelegramCommand.HELP.isEnabled()
                && TelegramCommand.HELP.getValue().equals(update.message().text());

    }

    @Override
    public void handle(Update update, TelegramBot telegramBot) {
        Long chatId = update.message().chat().id();

        log.atInfo()
                .setMessage("User calls /help command")
                .addKeyValue("user_ud", update.message().from().id())
                .log();

        String commandList = Arrays.stream(TelegramCommand.values())
                .filter(TelegramCommand::isEnabled)
                .map(c -> c.getValue() + " - " + c.getDescription())
                .collect(Collectors.joining("\n"));

        telegramBot.execute(new SendMessage(chatId, "Доступные команды: \n" + commandList));
    }
}
