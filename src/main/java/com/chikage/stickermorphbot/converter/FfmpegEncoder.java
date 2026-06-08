package com.chikage.stickermorphbot.converter;

import com.chikage.stickermorphbot.process.ProcessRunner;
import com.chikage.stickermorphbot.properties.ConversionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FfmpegEncoder {

    private final ProcessRunner processRunner;
    private final ConversionProperties props;

    public void encode(List<String> sourceInput, Path output, ConversionFormat format) {

        List<String> cmd = new ArrayList<>();
        cmd.add(props.getFfmpegPath());
        cmd.add("-y"); cmd.add("-hide_banner"); cmd.add("-loglevel"); cmd.add("error");
        cmd.addAll(sourceInput);

        switch (format) {
            case WEBM -> cmd.addAll(List.of(
                    "-c:v", "libvpx-vp9", "-pix_fmt", "yuva420p", "-auto-alt-ref", "0",
                    "-b:v", "0", "-crf", String.valueOf(props.getCrf())));
            case GIF -> cmd.addAll(List.of(
                    "-vf", "split[s0][s1];[s0]palettegen=reserve_transparent=1[p];[s1][p]paletteuse=alpha_threshold=128"));
            case MP4 -> cmd.addAll(List.of(
                    "-f", "lavfi", "-i", "color=c=white",
                    "-filter_complex", "[1:v][0:v]scale2ref[bg][fg];[bg][fg]overlay=shortest=1,format=yuv420p",
                    "-c:v", "libx264", "-pix_fmt", "yuv420p"));
            case PNG -> cmd.addAll(List.of("-frames:v", "1"));
        }
        cmd.add(output.toString());
        processRunner.run(cmd, output.getParent(), props.getProcessTimeout());
    }
}
