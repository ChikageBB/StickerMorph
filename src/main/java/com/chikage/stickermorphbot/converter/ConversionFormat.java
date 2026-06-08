package com.chikage.stickermorphbot.converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConversionFormat {

    WEBM("WEBM", "🟢 WEBM (прозрачный)", "out.webm"),
    GIF("GIF",  "🖼 GIF",               "out.gif"),
    MP4("MP4",  "🎬 MP4",               "out.mp4"),
    PNG("PNG", "🖼 PNG", "out.png");

    private final String code;
    private final String label;
    private final String fileName;

    public static ConversionFormat fromCode(String code) {
        for (ConversionFormat f : values()) {
            if (f.code.equals(code)) {
                return f;
            }
        }
        return null;
    }
}
