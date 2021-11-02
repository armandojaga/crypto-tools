package dev.armando.cryptotools.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MathUtilTest {

    @Test
    public void gdc_with_gdc() {
        int result = MathUtil.gdc(10, 15, 30);
        assertThat(result).isEqualTo(5);
    }

    @Test
    public void gdc_without_gdc() {
        int result = MathUtil.gdc(9, 14, 30);
        assertThat(result).isEqualTo(1);
    }

}
