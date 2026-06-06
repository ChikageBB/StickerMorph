package com.chikage.stickermorphbot.dispatcher;

import com.chikage.stickermorphbot.handler.UnknownCommandHandler;
import com.chikage.stickermorphbot.handler.UpdateHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramUpdateDispatcher {

    private final List<UpdateHandler> updateHandlers;

    public void dispatch(Update update, TelegramBot telegramBot) {

        updateHandlers.stream()
                .filter(h -> (!(h instanceof UnknownCommandHandler)))
                .filter(h -> h.support(update))
                .findFirst()
                .or(() -> updateHandlers.stream()
                        .filter(h -> (h instanceof UnknownCommandHandler))
                        .filter(h -> h.support(update))
                        .findFirst()
                )
                .ifPresent(h -> {
                    h.handle(update, telegramBot);
                });
    }
}
