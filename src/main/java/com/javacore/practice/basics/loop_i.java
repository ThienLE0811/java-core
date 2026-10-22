package com.javacore.practice.basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class loop_i {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        int N = Integer.parseInt(bufferedReader.readLine().trim());
        if (2 <= N && N <= 20) {
            for (int i = 1; i <= 10; i++) {

                int result = i * N;
                System.out.printf("%d x %d = %d%n", N, i, result);

            }
        }

        bufferedReader.close();
    }
}
