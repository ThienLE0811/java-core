package com.javacore.first_steps.first_java_code;

public class Calculator {

    public static double average(int[] numbers) {
        if (numbers.length == 0) {
            return 0;
        }
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }

        double avg = (double) sum / numbers.length;
        return avg;
    }
}