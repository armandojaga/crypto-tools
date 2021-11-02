package dev.armando.cryptotools.util;


import lombok.experimental.UtilityClass;
import org.apache.commons.math3.util.ArithmeticUtils;

@UtilityClass
public class MathUtil {

    public int gdc(Integer... numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException();
        }
        int result = numbers[0];
        boolean skip = true;
        for (int number : numbers) {
            if (skip) {
                skip = false;
                continue;
            }

            result = ArithmeticUtils.gcd(result, number);
        }
        return result;
    }
}
