package com.trueshot.media_process.exception;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CustomErrorResponse {
    private final Error error;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public CustomErrorResponse(Error error) {
        this.error = error;
    }
}
