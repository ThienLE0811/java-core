package com.javacore.practice.basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class loops {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        int q = Integer.parseInt(bufferedReader.readLine().trim());

        for (int query = 0; query < q; query++) {
            String[] parts = bufferedReader.readLine().trim().split("\\s+");
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            int n = Integer.parseInt(parts[2]);
            int s = a;
            int pow = 1;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                s += b * pow;
                sb.append(s).append(" ");
                pow *= 2;
            }

            System.out.println(sb.toString().trim());
        }

        bufferedReader.close();
    }
}

