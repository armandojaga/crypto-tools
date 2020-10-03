package dev.armando.cryptotools.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageMapping {
    private String language;
    private List<LetterFrequency> letterFrequencies;
}
