package dev.armando.cryptotools.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.armando.cryptotools.models.Frequency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
public class FrequencyConfig {

    @Bean
    public List<Frequency> frequencies() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File dataFile = new ClassPathResource("frequencies.json").getFile();
        return objectMapper.readValue(dataFile, new TypeReference<>() {
        });
    }
}
