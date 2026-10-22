package com.javacore.practice.basics;

import java.util.Scanner;

public class output_formatting {
    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        Scanner sc = new Scanner(System.in);

        System.out.println("================================");
        for (int k = 0; k < 3; k++) {
            String s = sc.next();
            int x = sc.nextInt();
            System.out.printf("%-15s%03d %n", s, x);

        }
        System.out.println("================================");
    }
}
