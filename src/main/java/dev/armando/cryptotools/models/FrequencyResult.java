package dev.armando.cryptotools.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrequencyResult {
    private String term;
    private long count;
    private double percentage;
}
