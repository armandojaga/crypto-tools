package dev.armando.cryptotools.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Separation {
    LETTERS(1),
    BIGRAMS(2),
    TRIGRAMS(3);

    @JsonValue
    private final int value;

    Separation(int value) {
        this.value = value;
    }
}
