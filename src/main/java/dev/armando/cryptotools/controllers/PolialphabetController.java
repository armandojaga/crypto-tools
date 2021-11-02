package dev.armando.cryptotools.controllers;

import dev.armando.cryptotools.services.PoliaphabetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/polialphabet")
public class PolialphabetController {

    private final PoliaphabetService poliaphabetService;

    public PolialphabetController(PoliaphabetService poliaphabetService) {
        this.poliaphabetService = poliaphabetService;
    }

    @PostMapping("/keysize")
    public ResponseEntity<Integer> keySize(@NotNull @RequestBody String texto) {
        return ResponseEntity.ok(poliaphabetService.keySize(texto));
    }

    @PostMapping("/fixed")
    public ResponseEntity<List<String>> fixed(@NotNull @RequestBody String texto, @RequestParam int size) {
        return ResponseEntity.ok(poliaphabetService.fixed(texto, size));
    }
}
