package dev.armando.cryptotools.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class ExtractionUtil {

    public List<String> extract(String text, int separation) {
        return IntStream.rangeClosed(0, text.length() - separation)
                .mapToObj(i -> text.substring(i, i + separation))
                .collect(Collectors.toList());
    }
}
