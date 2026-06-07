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

    public void encode(Path framesDir, double fps, Path output, ConversionFormat format) {

        String input = framesDir.resolve("frame_%05d.png").toString();

        List<String> command = switch (format) {
            case WEBM -> webmCommand(fps, input, output);
            case GIF -> gifCommand(fps, input, output);
            case MP4 -> mp4Command(fps, input, output);
        };

        processRunner.run(command, output.getParent(), props.getProcessTimeout());
    }

    private List<String> webmCommand(double fps, String input, Path output) {
        return List.of(props.getFfmpegPath(), "-y", "-hide_banner", "-loglevel", "error",
                "-framerate", String.valueOf(fps), "-i", input,
                "-c:v", "libvpx-vp9", "-pix_fmt", "yuva420p", "-auto-alt-ref", "0",
                "-b:v", "0", "-crf", String.valueOf(props.getCrf()),
                output.toString());
    }

    private List<String> gifCommand(double fps, String input, Path output) {
        return List.of(props.getFfmpegPath(), "-y", "-hide_banner", "-loglevel", "error",
                "-framerate", String.valueOf(fps), "-i", input,
                "-vf", "split[s0][s1];[s0]palettegen=reserve_transparent=1[p];[s1][p]paletteuse=alpha_threshold=128",
                output.toString());
    }

    private List<String> mp4Command(double fps, String input, Path output) {
        return List.of(props.getFfmpegPath(), "-y", "-hide_banner", "-loglevel", "error",
                "-framerate", String.valueOf(fps), "-i", input,
                "-f", "lavfi", "-i", "color=c=white",
                "-filter_complex", "[1:v][0:v]scale2ref[bg][fg];[bg][fg]overlay=shortest=1,format=yuv420p",
                "-c:v", "libx264", "-pix_fmt", "yuv420p",
                output.toString());
    }

}
