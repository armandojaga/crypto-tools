package dev.armando.cryptotools.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.armando.cryptotools.models.Frequency;
import dev.armando.cryptotools.models.FrequencyResult;
import dev.armando.cryptotools.models.enums.Language;
import dev.armando.cryptotools.models.enums.Separation;
import dev.armando.cryptotools.models.enums.Sorting;
import dev.armando.cryptotools.responses.FrequencyResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MonoalphabetService {

    private static final long MOST_RELEVANT_RESULT_AMOUNT = 20;

    private final List<Frequency> frequencies;

    DecimalFormat df;

    public MonoalphabetService(List<Frequency> frequencies) {
        this.frequencies = frequencies;
        df = new DecimalFormat("##.###");
    }

    public FrequencyResponse processFrequencies(String text, Sorting sorting, Long limit) {
        if (Objects.isNull(sorting)) {
            sorting = Sorting.PERCENTAGE;
        }

        if (Objects.isNull(limit) || limit == 0) {
            limit = MOST_RELEVANT_RESULT_AMOUNT;
        }

        List<String> letters = extract(text, Separation.LETTERS, Long.MAX_VALUE);
        List<String> brigrams = extract(text, Separation.BIGRAMS, limit);
        List<String> trigrams = extract(text, Separation.TRIGRAMS, limit);

        List<FrequencyResult> letterFrequency = calculatePercentages(letters, sorting);
        List<FrequencyResult> bigramsFrequency = calculatePercentages(brigrams);
        List<FrequencyResult> trigramsFrequency = calculatePercentages(trigrams);

        List<Double> percentages = letterFrequency.stream().map(FrequencyResult::getPercentage).collect(Collectors.toList());
        Map<String, Double> languageProbabilities = getProbableLanguage(percentages);
        String probableLanguage = Objects.requireNonNull(languageProbabilities).entrySet().stream().min(Map.Entry.comparingByValue()).orElseThrow().getKey();
        Language language = Language.fromString(probableLanguage);
        Objects.requireNonNull(language);

        return FrequencyResponse.builder()
                .probableLanguage(language.getName())
                .languageProbabilities(languageProbabilities)
                .letters(letterFrequency)
                .bigrams(bigramsFrequency)
                .trigrams(trigramsFrequency)
                .build();
    }

    public List<String> getBigrams(Language language) throws IOException {
        validateParameter(language);
        ObjectMapper objectMapper = new ObjectMapper();
        File dataFile = new ClassPathResource("frequent-bigrams.json").getFile();
        return Arrays.asList(objectMapper.convertValue(objectMapper.readTree(dataFile).get(language.getLocale()), String[].class));
    }

    public List<String> getTrigrams(Language language) throws IOException {
        validateParameter(language);
        ObjectMapper objectMapper = new ObjectMapper();
        File dataFile = new ClassPathResource("frequent-trigrams.json").getFile();
        return Arrays.asList(objectMapper.convertValue(objectMapper.readTree(dataFile).get(language.getLocale()), String[].class));
    }

    private void validateParameter(Object parameter) {
        if (Objects.isNull(parameter)) {
            throw new InvalidParameterException();
        }
    }

    private Map<String, Double> getProbableLanguage(List<Double> frequencies) {
        if (CollectionUtils.isEmpty(frequencies)) {
            return null;
        }
        Map<String, Double> probabilities = new HashMap<>();
        for (Frequency f : this.frequencies) {
            double probability = 0;
            for (int i = 0; i < f.getValues().size(); i++) {
                double occurrence = 0;
                if (i < frequencies.size()) {
                    occurrence = frequencies.get(i);
                }
                probability += Math.pow(f.getValues().get(i) - occurrence, 2);
            }
            probabilities.put(f.getLanguage(), round(probability));
        }
        return probabilities;
    }

    private List<FrequencyResult> calculatePercentages(List<String> terms) {
        return calculatePercentages(terms, null);
    }

    private List<FrequencyResult> calculatePercentages(List<String> terms, Sorting sorting) {
        Comparator<Map.Entry<String, Long>> comparator = Map.Entry.<String, Long>comparingByValue().reversed();
        if (sorting == Sorting.ALPHABETICAL) {
            comparator = Map.Entry.<String, Long>comparingByKey();
        }

        Map<String, Long> counts = terms.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        double denominator = terms.size();
        return counts.entrySet().stream()
                .sorted(comparator) // higher percentage first
                .map(e -> new FrequencyResult(e.getKey(), e.getValue(), round(e.getValue() / denominator * 100)))
                .collect(Collectors.toList());
    }

    private List<String> extract(String text, Separation separation, long limit) {
        return IntStream.rangeClosed(0, text.length() - separation.getValue())
                .mapToObj(i -> text.substring(i, i + separation.getValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double round(double toRound) {
        return Double.parseDouble(df.format(toRound));
    }
}
