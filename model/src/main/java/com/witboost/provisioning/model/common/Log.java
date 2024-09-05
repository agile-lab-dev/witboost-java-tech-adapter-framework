package com.witboost.provisioning.model.common;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Log {
    private final OffsetDateTime timestamp;
    private final LogLevelEnum level;
    private final String message;
    private Optional<String> phase = Optional.empty();

    public enum LogLevelEnum {
        DEBUG,
        INFO,
        WARNING,
        ERROR,
    }
}
