package dev.armando.cryptotools.responses;

import dev.armando.cryptotools.models.FrequencyResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrequencyResponse {
    private String probableLanguage;
    private List<FrequencyResult> letters;
    private List<FrequencyResult> bigrams;
    private List<FrequencyResult> trigrams;
}
