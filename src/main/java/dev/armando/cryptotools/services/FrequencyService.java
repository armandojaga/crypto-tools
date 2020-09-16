package dev.armando.cryptotools.services;

import dev.armando.cryptotools.models.Frequency;
import dev.armando.cryptotools.models.FrequencyResult;
import dev.armando.cryptotools.models.enums.Separation;
import dev.armando.cryptotools.models.enums.Sorting;
import dev.armando.cryptotools.responses.FrequencyResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FrequencyService {

    private final List<Frequency> frequencies;

    public FrequencyService(List<Frequency> frequencies) {
        this.frequencies = frequencies;
    }

    private String getProbableLanguage(List<Double> frequencies) {
        if (CollectionUtils.isEmpty(frequencies)) {
            return "Unknown";
        }
        Map<String, Double> probabilities = new HashMap<>();
        for (Frequency f : this.frequencies) {
            double probability = 0;
            for (int i = 0; i < f.getValues().size(); i++) {
                double ocurrence = 0;
                if (i < frequencies.size()) {
                    ocurrence = frequencies.get(i);
                }
                probability += Math.pow(f.getValues().get(i) - ocurrence, 2);
            }
            probabilities.put(f.getLanguage(), probability);
        }
        return probabilities.entrySet().stream().min(Map.Entry.comparingByValue()).orElseThrow().getKey();
    }

    public FrequencyResponse getFrequencies(String texto, Sorting sorting) {
        if (Objects.isNull(sorting)) {
            sorting = Sorting.PERCENTAGE;
        }

        List<String> letters = extract(texto, Separation.LETTERS);
        List<String> brigrams = extract(texto, Separation.BIGRAMS);
        List<String> trigrams = extract(texto, Separation.TRIGRAMS);

        List<FrequencyResult> letterFrequency = calculatePercentages(letters);
        List<FrequencyResult> bigramsFrequency = calculatePercentages(brigrams);
        List<FrequencyResult> trigramsFrequency = calculatePercentages(trigrams);

        List<Double> percentages = letterFrequency.stream().map(FrequencyResult::getPercentage).collect(Collectors.toList());
        String probableLanguage = getProbableLanguage(percentages);

        return FrequencyResponse.builder()
                .probableLanguage(probableLanguage)
                .letters(letterFrequency)
                .bigrams(bigramsFrequency)
                .trigrams(trigramsFrequency)
                .build();
    }

    private List<FrequencyResult> calculatePercentages(List<String> terms) {
        Map<String, Long> counts = terms.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        double denominator = terms.size();
        List<FrequencyResult> results = new ArrayList<>();
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // higher percentage first
                .map(e -> new FrequencyResult(e.getKey(), e.getValue(), e.getValue() / denominator * 100))
                .collect(Collectors.toList());
    }

    private List<String> extract(String texto, Separation separation) {
        return IntStream.rangeClosed(0, texto.length() - separation.getValue())
                .mapToObj(i -> texto.substring(i, i + separation.getValue()))
                .collect(Collectors.toList());
    }
}
