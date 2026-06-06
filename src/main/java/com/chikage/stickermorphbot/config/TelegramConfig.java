package com.chikage.stickermorphbot.config;

import com.chikage.stickermorphbot.properties.TelegramProperties;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramBot telegramBot(TelegramProperties props) {
        var builder = new TelegramBot.Builder(props.getToken())
                .apiUrl(props.getUrl())
                .updateListenerSleep(props.getUpdateListenerSleep().toMillis());

                if (props.isDebug()) {
                    builder.debug();
                }

                return builder.build();
    }
}
