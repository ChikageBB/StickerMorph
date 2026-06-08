package com.chikage.stickermorphbot.converter;

import com.pengrad.telegrambot.model.Sticker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum StickerType {

    ANIMATED(List.of(ConversionFormat.WEBM, ConversionFormat.MP4, ConversionFormat.GIF)),
    VIDEO(List.of(ConversionFormat.GIF, ConversionFormat.MP4)),
    STATIC(List.of(ConversionFormat.PNG));

    private final List<ConversionFormat> formats;

    public static StickerType from(Sticker sticker) {
        if (Boolean.TRUE.equals(sticker.isAnimated())) return ANIMATED;
        if (Boolean.TRUE.equals(sticker.isVideo())) return VIDEO;
        return STATIC;
    }
}
