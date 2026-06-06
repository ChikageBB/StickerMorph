package com.chikage.stickermorphbot.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TelegramCommand {
    START("/start", "Starting bot", true),
    HELP("/help", "Help", true);


    private final String value;
    private final String description;
    private final boolean isEnabled;

    public static TelegramCommand fromValue(String value) {
        for (TelegramCommand command: values()) {
            if (command.value.equals(value)) {
                return command;
            }
        }
        return null;
    }
}
