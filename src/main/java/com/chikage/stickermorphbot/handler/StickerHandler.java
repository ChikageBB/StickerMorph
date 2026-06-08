package com.chikage.stickermorphbot.handler;

import com.chikage.stickermorphbot.converter.ConversionFormat;
import com.chikage.stickermorphbot.converter.StickerType;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Sticker;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyParameters;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class StickerHandler implements UpdateHandler{

    @Override
    public boolean support(Update update) {
       return update.message() != null && update.message().sticker() != null;
    }

    @Override
    public void handle(Update update, TelegramBot telegramBot) {
        Long chatId = update.message().chat().id();
        Integer stickerMessageId = update.message().messageId();
        Sticker sticker = update.message().sticker();

        StickerType stickerType = StickerType.from(sticker);

        log.info("Пришел стикер от {}, {}", chatId, sticker.emoji());

        InlineKeyboardButton[] buttons = stickerType.getFormats().stream()
                .map(f -> new InlineKeyboardButton(f.getLabel())
                        .callbackData("conv:" + f.getCode()))
                .toArray(InlineKeyboardButton[]::new);

        telegramBot.execute(new SendMessage(chatId, "В какой формат конвертировать?")
                .replyParameters(new ReplyParameters(stickerMessageId))
                .replyMarkup(new InlineKeyboardMarkup(buttons)));
    }
}
