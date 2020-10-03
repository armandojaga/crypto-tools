package dev.armando.cryptotools.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.armando.cryptotools.models.FrequencyResult;
import dev.armando.cryptotools.models.LanguageMapping;
import dev.armando.cryptotools.models.LetterFrequency;
import dev.armando.cryptotools.models.enums.Language;
import dev.armando.cryptotools.models.enums.Separation;
import dev.armando.cryptotools.models.enums.Sorting;
import dev.armando.cryptotools.responses.StatisticsResponse;
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

    private final List<LanguageMapping> frequencies;

    DecimalFormat df;

    public MonoalphabetService(List<LanguageMapping> frequencies) {
        this.frequencies = frequencies;
        df = new DecimalFormat("##.###");
    }

    public StatisticsResponse processFrequencies(String text, Sorting sorting, Long limit) {
        if (Objects.isNull(sorting)) {
            sorting = Sorting.PERCENTAGE;
        }

        if (Objects.isNull(limit) || limit == 0) {
            limit = MOST_RELEVANT_RESULT_AMOUNT;
        }

        List<String> letters = extract(text, Separation.LETTERS);
        List<String> brigrams = extract(text, Separation.BIGRAMS);
        List<String> trigrams = extract(text, Separation.TRIGRAMS);

        List<FrequencyResult> letterFrequency = calculatePercentages(letters, sorting);
        List<FrequencyResult> bigramsFrequency = calculatePercentages(brigrams, limit);
        List<FrequencyResult> trigramsFrequency = calculatePercentages(trigrams, limit);

        List<Double> percentages = letterFrequency.stream().map(FrequencyResult::getPercentage).collect(Collectors.toList());
        Map<String, Double> languageProbabilities = getProbableLanguage(percentages);
        String probableLanguage = Objects.requireNonNull(languageProbabilities).entrySet().stream().min(Map.Entry.comparingByValue()).orElseThrow().getKey();
        Language language = Language.fromString(probableLanguage);
        Objects.requireNonNull(language);

        return StatisticsResponse.builder()
                .probableLanguage(language.getName())
                .probableLanguageLocale(language.getLocale())
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
        for (LanguageMapping f : this.frequencies) {
            double probability = 0;
            for (int i = 0; i < f.getLetterFrequencies().size(); i++) {
                double occurrence = 0;
                if (i < frequencies.size()) {
                    occurrence = frequencies.get(i);
                }
                probability += Math.pow(f.getLetterFrequencies().get(i).getFrequency() - occurrence, 2);
            }
            probabilities.put(f.getLanguage(), round(probability));
        }
        return probabilities;
    }

    private List<FrequencyResult> calculatePercentages(List<String> terms, Sorting sorting) {
        return calculatePercentages(terms, sorting, Long.MAX_VALUE);
    }

    private List<FrequencyResult> calculatePercentages(List<String> terms, long limit) {
        return calculatePercentages(terms, null, limit);
    }

    private List<FrequencyResult> calculatePercentages(List<String> terms, Sorting sorting, long limit) {
        Comparator<Map.Entry<String, Long>> comparator = Map.Entry.<String, Long>comparingByValue().reversed();
        if (sorting == Sorting.ALPHABETICAL) {
            comparator = Map.Entry.comparingByKey();
        }

        Map<String, Long> counts = terms.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        double denominator = terms.size();
        return counts.entrySet().stream()
                .sorted(comparator) // higher percentage first
                .map(e -> new FrequencyResult(e.getKey(), e.getValue(), round(e.getValue() / denominator * 100)))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<String> extract(String text, Separation separation) {
        return IntStream.rangeClosed(0, text.length() - separation.getValue())
                .mapToObj(i -> text.substring(i, i + separation.getValue()))
                .collect(Collectors.toList());
    }

    private double round(double toRound) {
        return Double.parseDouble(df.format(toRound));
    }

    public List<LetterFrequency> getLetters(Language language) {
        return frequencies.stream().filter(l -> l.getLanguage().equals(language.getLocale())).findFirst().orElseThrow().getLetterFrequencies();
    }
}
