package dev.armando.cryptotools.responses;

import dev.armando.cryptotools.models.FrequencyResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsResponse {
    private String probableLanguage;
    private Map<String, Double> languageProbabilities;
    private List<FrequencyResult> letters;
    private List<FrequencyResult> bigrams;
    private List<FrequencyResult> trigrams;
}
