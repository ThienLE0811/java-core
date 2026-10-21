package com.javacore.practice.basics;

import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/* Nhập dữ liệu integer để check */
public class if_else {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        int N = Integer.parseInt(bufferedReader.readLine().trim());
        if (N % 2 > 0) {
            System.out.print("Weird");
            return;
        }
        if ((2 < N && N < 5) || N > 20) {
            System.out.print("Not Weird");
            return;
        } else if (6 <= N && N <= 20) {
            System.out.print("Weird");
            return;
        }

        bufferedReader.close();
    }
}
