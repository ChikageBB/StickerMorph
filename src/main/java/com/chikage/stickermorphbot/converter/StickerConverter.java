package com.chikage.stickermorphbot.converter;

import com.chikage.stickermorphbot.properties.ConversionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StickerConverter {

    private final LottieRenderer lottieRenderer;
    private final FfmpegEncoder ffmpegEncoder;
    private final ConversionProperties props;

    public record ConversionResult(Path webm, Path workDir) implements Closeable {

        @Override
        public void close() throws IOException {
            deleteRecursively(workDir);
        }

        private static void deleteRecursively(Path dir) {
            if (dir == null || !Files.exists(dir)) {
                return;
            }

            try (var path = Files.walk(dir)) {
                path.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                log.warn("Не удалось удалить: {}", p, e);
                            }
                        });
            } catch (IOException e) {
                log.warn("Не удалось почистить: {}", dir, e);
            }
        }
    }

    public ConversionResult convert(byte[] tgsBytes) {
        Path workDir = createWorkDir();
        try {
            Path tgsFile = workDir.resolve("sticker.tgs");
            Files.write(tgsFile, tgsBytes);

            Path framesDir = Files.createDirectories(workDir.resolve("frames"));
            double fps = lottieRenderer.render(tgsFile, framesDir);

            Path webm = workDir.resolve("out.webm");
            ffmpegEncoder.encode(framesDir, fps, webm);

            log.info("Конвертация готова: {} (fps={})", webm, fps);
            return new ConversionResult(webm, workDir);
        } catch (IOException e) {
            ConversionResult.deleteRecursively(workDir);
            throw new UncheckedIOException("Ошибка конвертации стикера", e);
        } catch (RuntimeException e) {
            ConversionResult.deleteRecursively(workDir);
            throw e;
        }
    }


    private Path createWorkDir() {
        try {
            Path base = Path.of(props.getWorkDir());
            Files.createDirectories(base);
            return Files.createDirectories(base.resolve("req-" + UUID.randomUUID()));
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось создать рабочую папку", e);
        }
    }

}
