package com.chikage.stickermorphbot.handler;

import com.chikage.stickermorphbot.converter.ConversionFormat;
import com.chikage.stickermorphbot.converter.StickerType;
import com.chikage.stickermorphbot.service.ConversionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackQueryHandler implements UpdateHandler {

    private final ConversionService conversionService;
    private final ThreadPoolTaskExecutor conversionExecutor;

    @Override
    public boolean support(Update update) {
        return update.callbackQuery() != null &&
                update.callbackQuery().data() != null &&
                update.callbackQuery().data().startsWith("conv:");
    }

    @Override
    public void handle(Update update, TelegramBot telegramBot) {
        CallbackQuery cq = update.callbackQuery();

        Message botMessage = cq.message();
        ConversionFormat format = ConversionFormat.fromCode(cq.data().substring("conv:".length()));

        if (botMessage == null
                || botMessage.replyToMessage() == null
                || botMessage.replyToMessage().sticker() == null
                || format == null) {
            telegramBot.execute(new SendMessage(cq.from().id(),
                    "Не вижу исходный стикер (возможно, он слишком старый) — пришли его заново 🙏"));
            return;
        }

        Long chatId = botMessage.chat().id();

        telegramBot.execute(new DeleteMessage(chatId, botMessage.messageId()));

        var sticker = botMessage.replyToMessage().sticker();
        String fileId = sticker.fileId();
        String uniqueId = sticker.fileUniqueId();
        StickerType stickerType = StickerType.from(sticker);

        try {
            conversionExecutor.execute(() -> process(chatId, uniqueId, fileId, stickerType, format, telegramBot));
        } catch (TaskRejectedException e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Сейчас много запросов, попробуй через минуту 🙏"));
        }
    }

    private void process(Long chatId, String uniqueId, String fileId, StickerType stickerType, ConversionFormat format, TelegramBot telegramBot) {
        telegramBot.execute(new SendMessage(chatId, "⏳ Конвертирую в " + format.getCode() + "..."));
        try {
            conversionService.convertAndSend(chatId, uniqueId, fileId, stickerType, format, telegramBot);
        } catch (Exception e) {
            log.error("Ошибка конвертации, chatId={}", chatId, e);
            telegramBot.execute(new SendMessage(chatId, "Не получилось сконвертировать стикер 😔"));
        }
    }
}
