FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg python3 python3-venv \
    && rm -rf /var/lib/apt/lists/*

RUN python3 -m venv /opt/venv
RUN /opt/venv/bin/pip install --no-cache-dir rlottie-python pillow

WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
COPY scripts/ /app/scripts/

ENV APP_CONVERSION_FFMPEG_PATH=/usr/bin/ffmpeg \
    APP_CONVERSION_PYTHON_PATH=/opt/venv/bin/python3 \
    APP_CONVERSION_RENDER_SCRIPT_PATH=/app/scripts/tgs_to_frames.py \
    APP_CONVERSION_WORK_DIR=/tmp/stickermorph

ENTRYPOINT ["java", "-jar", "app.jar"]