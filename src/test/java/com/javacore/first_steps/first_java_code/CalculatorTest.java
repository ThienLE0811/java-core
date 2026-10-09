package com.javacore.first_steps.first_java_code;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {
    @Test
    void average() {
        // Arrange: chuẩn bị input
        int[] numbers = {1, 2, 3};

        // Act: gọi method cần test
        double result = Calculator.average(numbers);
        IO.println("result " + result);
        // Assert: kiểm tra kết quả mong đợi
        assertEquals(2.0, result);

    }

    // ví dụ của dev.java
    @Test
    void shouldCalculateAverageOfPositiveNumbers() {
        int[] numbers = {1, 2, 3, 4, 5};

        double actualAverage = Calculator.average(numbers);
        IO.println("actualAverage " + actualAverage);
        double expectedAverage = 3.0;
        assertEquals(expectedAverage, actualAverage);
    }

    // ví dụ của dev.java
    @Test
    void shouldReturnZeroAverageForEmptyArray() {
        int[] numbers = {};

        double actualAverage = Calculator.average(numbers);
        IO.println("actualAverage2 " + actualAverage);
        double expectedAverage = 0;
        assertEquals(expectedAverage, actualAverage);
    }
}