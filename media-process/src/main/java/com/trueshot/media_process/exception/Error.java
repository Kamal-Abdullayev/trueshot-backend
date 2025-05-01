package com.trueshot.media_process.exception;

import java.util.List;

public record Error(
        String errorCode,
        List<String> errorMessages
) {
}
