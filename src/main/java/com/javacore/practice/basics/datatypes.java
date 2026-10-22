package com.javacore.practice.basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class datatypes {

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        int t = Integer.parseInt(bufferedReader.readLine().trim());

        for (int i = 0; i < t; i++) {
            String token = bufferedReader.readLine().trim();
            try {
                long value = Long.parseLong(token);
                System.out.println(value + " can be fitted in:");
                if (fits(value, Byte.MIN_VALUE, Byte.MAX_VALUE)) {
                    System.out.println("* byte");
                }
                if (fits(value, Short.MIN_VALUE, Short.MAX_VALUE)) {
                    System.out.println("* short");
                }
                if (fits(value, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
                    System.out.println("* int");
                }

                System.out.println("* long");

            } catch (NumberFormatException e) {
                System.out.println(token + " can't be fitted anywhere.");
            }
        }

        bufferedReader.close();
    }

    private static boolean fits(long value, long min, long max) {
        return value >= min && value <= max;
    }
}
