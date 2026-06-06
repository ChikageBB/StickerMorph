# 🎭 StickerMorphBot

Телеграм-бот, который конвертирует анимированные стикеры Telegram (`.tgs`)
в видео `.webm` (VP9) **с сохранением прозрачности**.

## 🎬 Демо

<!-- сюда вставить видео, см. ниже -->
https://github.com/USER/REPO/assets/XXXX/your-video-id

## ✨ Возможности
- Приём анимированных стикеров (`.tgs`) в чате
- Конвертация в `.webm` (VP9, прозрачный фон сохраняется)
- Асинхронная обработка с ограниченной очередью (не блокирует приём сообщений)
- Команды `/start`, `/help`

## ⚙️ Как это работает

1. Бот скачивает стикер по `file_id`
2. `rlottie-python` рисует кадры анимации
3. `ffmpeg` склеивает их в `.webm` с прозрачностью
4. Готовый файл отправляется пользователю, временные файлы удаляются

## 🛠️ Стек
- Java 17, Spring Boot 4
- [pengrad/java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)
- ffmpeg, Python 3 (`rlottie-python`, `pillow`)

## 📦 Требования
```bash
brew install ffmpeg
pip3 install rlottie-python pillow