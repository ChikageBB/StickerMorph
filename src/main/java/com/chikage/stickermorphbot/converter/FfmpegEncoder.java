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
public class FfmpegEncoder {

    private final ProcessRunner processRunner;
    private final ConversionProperties props;

    public void encode(Path framesDir, double fps, Path outputWebm) {
        List<String> command = List.of(
                props.getFfmpegPath(),
                "-y",
                "-hide_banner",
                "-loglevel", "error",
                "-framerate", String.valueOf(fps),
                "-i", framesDir.resolve("frame_%05d.png").toString(),
                "-c:v", "libvpx-vp9",
                "-pix_fmt", "yuva420p",
                "-auto-alt-ref", "0",
                "-b:v", "0",
                "-crf", String.valueOf(props.getCrf()),
                outputWebm.toString()
        );

        processRunner.run(command, outputWebm.getParent(), props.getProcessTimeout());
    }

}
