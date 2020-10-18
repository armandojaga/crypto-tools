package dev.armando.cryptotools.services;

import dev.armando.cryptotools.models.Tuple;
import dev.armando.cryptotools.util.ExtractionUtil;
import dev.armando.cryptotools.util.MathUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PoliaphabetService {

    public Integer keySize(String text) {
        int iter = Math.max(27, text.length() / 27);
        int gcd = 1;

        for (int i = iter; i > 4; i--) {
            AtomicInteger ai = new AtomicInteger(1);

            List<Tuple> tuples = ExtractionUtil.extract(text, i)
                    .stream()
                    .map(s -> new Tuple(s, ai.getAndIncrement()))
                    .collect(Collectors.collectingAndThen(Collectors.groupingBy(Tuple::getTerm), m -> m.values().stream().filter(l -> l.size() > 1)))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            if (tuples.isEmpty()) {
                continue;
            }

            int gcdLocal = tuples.stream()
                    .collect(Collectors.collectingAndThen(Collectors.toMap(Tuple::getTerm, Tuple::getIndex,
                            (a, b) -> Math.abs(b - a)), s -> MathUtil.gdc(s.values().toArray(new Integer[0]))));
            if (gcdLocal == 1) {
                continue;
            }
            if (gcd == 1) {
                gcd = gcdLocal;
            } else {
                gcd = MathUtil.gdc(gcd, gcdLocal);
            }
        }
        return gcd;
    }

    public List<String> fixed(String text, int size) {
        return new ArrayList<>(ExtractionUtil.extract(text, size));
    }
}
