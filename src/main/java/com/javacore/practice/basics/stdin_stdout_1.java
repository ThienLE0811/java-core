package com.javacore.practice.basics;

import java.util.Scanner;

/* Nhập 3 số  và in ra 3 số */
public class stdin_stdout_1 {
    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        Scanner scanner = new Scanner(System.in);
        int myInt = scanner.nextInt();
        int myInt2 = scanner.nextInt();
        int myInt3 = scanner.nextInt();
        scanner.close();

        System.out.println(myInt);
        System.out.println(myInt2);
        System.out.println(myInt3);
    }
}
