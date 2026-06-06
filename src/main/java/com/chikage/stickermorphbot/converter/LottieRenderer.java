package com.chikage.stickermorphbot.converter;

import com.chikage.stickermorphbot.process.ProcessRunner;
import com.chikage.stickermorphbot.properties.ConversionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LottieRenderer {

    private final ProcessRunner processRunner;
    private final ConversionProperties props;

    public double render(Path tgsFile, Path framesDir) {
        List<String> command = List.of(
                props.getPythonPath(),
                props.getRenderScriptPath(),
                tgsFile.toString(),
                framesDir.toString()
        );

        ProcessRunner.ProcessResult result = processRunner.run(command,
                framesDir.getParent(), props.getProcessTimeout());

        return parseFps(result.stdout());
    }

    public double parseFps(String stdout) {
        try {
            String last = stdout.strip().lines()
                    .reduce((a, b) -> b)
                    .orElse("");

            double fps = Double.parseDouble(last.strip());
            return fps > 0 ? fps : props.getFallbackFps();
        } catch (NumberFormatException e) {
            log.warn("Не удалось распарсить fps из stdout: '{}', беру fallback {}",
                    stdout, props.getFallbackFps());
            return props.getFallbackFps();
        }
    }
}
