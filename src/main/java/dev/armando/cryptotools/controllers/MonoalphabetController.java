package dev.armando.cryptotools.controllers;

import dev.armando.cryptotools.models.LetterFrequency;
import dev.armando.cryptotools.models.enums.Language;
import dev.armando.cryptotools.models.enums.Sorting;
import dev.armando.cryptotools.responses.StatisticsResponse;
import dev.armando.cryptotools.services.MonoalphabetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/monoalphabet")
public class MonoalphabetController {

    private final MonoalphabetService monoalphabetService;

    public MonoalphabetController(MonoalphabetService monoalphabetService) {
        this.monoalphabetService = monoalphabetService;
    }

    @PostMapping("/statistics")
    public ResponseEntity<StatisticsResponse> processFrequency(@NotNull @RequestBody String texto,
                                                               @RequestParam(required = false) Sorting sorting,
                                                               @RequestParam(required = false) Long limit
    ) {
        StatisticsResponse response = monoalphabetService.processFrequencies(texto, sorting, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/letters")
    public ResponseEntity<List<LetterFrequency>> getLetters(@NotNull @RequestParam Language language) throws IOException {
        return ResponseEntity.ok(monoalphabetService.getLetters(language));
    }

    @GetMapping("/bigrams")
    public ResponseEntity<List<String>> getBigrams(@NotNull @RequestParam Language language) throws IOException {
        return ResponseEntity.ok(monoalphabetService.getBigrams(language));
    }

    @GetMapping("/trigrams")
    public ResponseEntity<List<String>> getTrigrams(@NotNull @RequestParam Language language) throws IOException {
        return ResponseEntity.ok(monoalphabetService.getTrigrams(language));
    }
}
