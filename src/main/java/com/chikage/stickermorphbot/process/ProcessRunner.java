package com.chikage.stickermorphbot.process;

import com.chikage.stickermorphbot.exception.ProcessExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ProcessRunner {

    public record ProcessResult(int exitCode, String stdout, String stderr) {}

    public ProcessResult run(List<String> command, Path workDir, Duration timeout) {
        log.debug("Running process: {}", command);

        Process process;
        try {
            process = new ProcessBuilder(command)
                    .directory(workDir.toFile())
                    .start();
        } catch (IOException e) {
            throw new ProcessExecutionException("Не удалось запустить процесс: " + command, e);
        }

        try {
            boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);

            if (!finished) {
                process.destroy();
                throw new ProcessExecutionException("Процесс превысил таймаут: " + command);
            }

            String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            int code = process.exitValue();

            if (code != 0) {
                throw new ProcessExecutionException(
                        "Процесс завершился с кодом " + code + ": " + command + "\nstderr: " + stderr);
            }

            return new ProcessResult(code, stdout, stderr);

        } catch (InterruptedException e) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
            throw new ProcessExecutionException("Процесс прерван: " + command, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
