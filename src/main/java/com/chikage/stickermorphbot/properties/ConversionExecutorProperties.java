package com.chikage.stickermorphbot.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@EqualsAndHashCode
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.conversion.executor")
public class ConversionExecutorProperties {

    @Min(1)
    @Positive
    private Integer poolSize;

    @Min(1)
    @Positive
    private Integer maxPoolSize;

    @Min(1)
    @Positive
    private Integer queueCapacity;

    @NotEmpty
    private String threadNamePrefix;
}
