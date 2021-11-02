package dev.armando.cryptotools.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Language {
    SPANISH("es", "Spanish"),
    ENGLISH("en", "English"),
    FRENCH("fr", "French"),
    GERMAN("de", "German");

    @JsonValue
    private final String locale;

    private final String name;

    Language(String locale, String name) {
        this.locale = locale;
        this.name = name;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Language fromString(String value) {
        for (Language language : Language.values()) {
            if (language.locale.equalsIgnoreCase(value)) {
                return language;
            }
        }
        return null;
    }
}
