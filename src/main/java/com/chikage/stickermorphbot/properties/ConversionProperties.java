package com.chikage.stickermorphbot.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "app.conversion")
public class ConversionProperties {

    @NotEmpty
    private String pythonPath;

    @NotEmpty
    private String renderScriptPath;

    @NotEmpty
    private String ffmpegPath;

    @NotEmpty
    private String workDir;

    @Positive
    private double fallbackFps = 30.0;

    @Positive
    private int crf = 30;

    private Duration processTimeout = Duration.ofSeconds(60);

}
