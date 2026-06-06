package com.chikage.stickermorphbot.listener;

import com.chikage.stickermorphbot.dispatcher.TelegramUpdateDispatcher;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdateListener {

    private final TelegramBot telegramBot;
    private final TelegramUpdateDispatcher dispatcher;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update: updates) {
                try {
                    dispatcher.dispatch(update, telegramBot);
                } catch (Exception e) {
                    log.error("Ошибка при обработке update: {}", update, e);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @PreDestroy
    public void stop() {
        telegramBot.removeGetUpdatesListener();
    }

}
