package dev.armando.cryptotools.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LetterFrequency {
    private String letter;
    private double frequency;
}
