package com.chikage.stickermorphbot.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Validated
@EqualsAndHashCode
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.telegram")
public class TelegramProperties {

    @URL
    @NotEmpty
    private String url;

    @NotEmpty
    private String token;

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration updateListenerSleep = Duration.ofSeconds(1);

    private boolean debug;
}
