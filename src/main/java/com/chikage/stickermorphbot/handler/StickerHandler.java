package com.chikage.stickermorphbot.handler;

import com.chikage.stickermorphbot.converter.StickerConverter;
import com.chikage.stickermorphbot.service.TelegramFileService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Sticker;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StickerHandler implements UpdateHandler{

    private final TelegramFileService fileService;
    private final StickerConverter stickerConverter;
    private final ThreadPoolTaskExecutor conversionExecutor;

    @Override
    public boolean support(Update update) {
       return update.message() != null && update.message().sticker() != null;
    }

    @Override
    public void handle(Update update, TelegramBot telegramBot) {
        Long chatId = update.message().chat().id();
        Sticker sticker = update.message().sticker();

        log.info("Пришел стикер от {}, {}", chatId, sticker.emoji());

        if (!sticker.isAnimated()) {
            telegramBot.execute(new SendMessage(chatId,
                    "Пока умею конвертировать только анимированные стикеры (.tgs) 🙏"));
            return;
        }

        String fileId = sticker.fileId();
        try {
            conversionExecutor.execute(() -> process(chatId, fileId, telegramBot));
        } catch (TaskRejectedException e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Сейчас много запросов, попробуй через минуту 🙏"));
        }
    }

    private void process(Long chatId, String fileId, TelegramBot telegramBot) {
        log.info("Начинаю конвертацию стикера, chatId={}", chatId);
        telegramBot.execute(new SendMessage(chatId, "⏳ Конвертирую стикер..."));

        try {
            byte[] tgsBytes = fileService.downloadFile(fileId);

            try (StickerConverter.ConversionResult result = stickerConverter.convert(tgsBytes)) {
                telegramBot.execute(new SendDocument(chatId, result.webm().toFile()));
            }
        } catch (Exception e) {
            log.error("Ошибка конвертации стикера, chatId={}", chatId, e);
            telegramBot.execute(new SendMessage(chatId,
                    "Не получилось сконвертировать стикер 😔"));
        }
    }

}
