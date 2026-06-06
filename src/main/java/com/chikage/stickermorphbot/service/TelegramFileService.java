package com.chikage.stickermorphbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramFileService {

    private final TelegramBot telegramBot;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public byte[] downloadFile(String fileId) {
        GetFileResponse response = telegramBot.execute(new GetFile(fileId));

        if (!response.isOk()) {
            throw new IllegalStateException(
                    "GetFile failed: " + response.errorCode() + " " + response.description());
        }

        String url = telegramBot.getFullFilePath(response.file());

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();

            HttpResponse<byte[]> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (resp.statusCode() != 200) {
                throw new IllegalStateException("Download failed, HTTP " + resp.statusCode());
            }
            return resp.body();
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка скачивания файла Telegram", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Скачивание прервано", e);
        }
    }
}
