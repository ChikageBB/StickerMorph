package com.chikage.stickermorphbot.service;

import com.chikage.stickermorphbot.cache.ConversionResultCache;
import com.chikage.stickermorphbot.converter.ConversionFormat;
import com.chikage.stickermorphbot.converter.StickerConverter;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendAnimation;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendVideo;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.chikage.stickermorphbot.cache.ConversionResultCache.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionService {

    private final TelegramFileService fileService;
    private final StickerConverter converter;
    private final ConversionResultCache resultCache;

    public void convertAndSend(Long chatId, String uniqueId, String fileId,
                               ConversionFormat format, TelegramBot bot) throws IOException {

        CachedFile cached = resultCache.get(uniqueId, format);
        if (cached != null) {
            log.info("Кэш-хит: uniqueId={}, format={}", uniqueId, format.getCode());
            resend(chatId, cached, bot);
            return;
        }

        byte[] tgsBytes = fileService.downloadFile(fileId);
        try (StickerConverter.ConversionResult result = converter.convert(tgsBytes, format)) {

            SendResponse response = bot.execute(new SendDocument(chatId, result.file().toFile()));
            CachedFile sent = extractFile(response.message());

            if (sent != null) {
                resultCache.put(uniqueId, format, sent);
                log.info("Закэшировано: uniqueId={}, format={}, kind={}", uniqueId, format.getCode(), sent.kind());
            }
        }
    }

    private void resend(Long chatId, CachedFile cached, TelegramBot bot)  {
        switch (cached.kind()) {
            case DOCUMENT -> bot.execute(new SendDocument(chatId, cached.fileId()));
            case VIDEO -> bot.execute(new SendVideo(chatId, cached.fileId()));
            case ANIMATION -> bot.execute(new SendAnimation(chatId, cached.fileId()));
        }
    }

    private CachedFile extractFile(Message message) {
        if (message == null) return  null;
        if (message.document() != null ) return new CachedFile(message.document().fileId(), Kind.DOCUMENT);
        if (message.video() != null ) return new CachedFile(message.video().fileId(), Kind.VIDEO);
        if (message.animation() != null ) return new CachedFile(message.animation().fileId(), Kind.ANIMATION);
        return null;
    }

}
