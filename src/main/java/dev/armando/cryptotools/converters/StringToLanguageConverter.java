package dev.armando.cryptotools.converters;

import dev.armando.cryptotools.models.enums.Language;
import org.springframework.core.convert.converter.Converter;

public class StringToLanguageConverter implements Converter<String, Language> {
    @Override
    public Language convert(String source) {
        return Language.fromString(source);
    }
}
