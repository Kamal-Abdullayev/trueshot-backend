package com.trueshot.post.exception;

import java.util.List;

public record Error(
        String errorCode,
        List<String> errorMessages
) {
}