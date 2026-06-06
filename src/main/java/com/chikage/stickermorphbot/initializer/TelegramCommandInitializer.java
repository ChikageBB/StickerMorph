package com.chikage.stickermorphbot.initializer;

import com.chikage.stickermorphbot.command.TelegramCommand;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramCommandInitializer {

    private final TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        try {
            BotCommand[] commands = Arrays.stream(TelegramCommand.values())
                    .filter(TelegramCommand::isEnabled)
                    .map(c -> new BotCommand(c.getValue(), c.getDescription()))
                    .toArray(BotCommand[]::new);

            telegramBot.execute(new SetMyCommands(commands));
            log.info("Bot commands initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize bot commands", e);
        }
    }
}
