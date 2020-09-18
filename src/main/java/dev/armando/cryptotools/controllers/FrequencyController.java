package dev.armando.cryptotools.controllers;

import dev.armando.cryptotools.models.enums.Sorting;
import dev.armando.cryptotools.responses.FrequencyResponse;
import dev.armando.cryptotools.services.FrequencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/frequency")
public class FrequencyController {

    private final FrequencyService frequencyService;

    public FrequencyController(FrequencyService frequencyService) {
        this.frequencyService = frequencyService;
    }

    @PostMapping
    public ResponseEntity<FrequencyResponse> get(@RequestBody String texto,
                                                 @RequestParam(required = false) Sorting sorting,
                                                 @RequestParam(required = false) Long limit
    ) {
        FrequencyResponse response = frequencyService.getFrequencies(texto, sorting, limit);
        return ResponseEntity.ok(response);
    }
}
