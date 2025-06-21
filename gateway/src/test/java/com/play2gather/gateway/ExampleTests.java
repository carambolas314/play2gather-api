package com.play2gather.gateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExampleTests {
    @Test
    public void testSomar() {
        Example calc = new Example();
        assertEquals(5, calc.somar(2, 3));
        assertEquals(-1, calc.somar(-2, 1));
    }

    @Test
    public void testSubtrair() {
        Example calc = new Example();
        assertEquals(1, calc.subtrair(3, 2));
        assertEquals(-3, calc.subtrair(-2, 1));
    }
}
