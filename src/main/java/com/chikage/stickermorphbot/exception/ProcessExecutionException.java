package com.chikage.stickermorphbot.exception;

public class ProcessExecutionException extends RuntimeException{
    public ProcessExecutionException(String message) {
        super(message);
    }

    public ProcessExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
