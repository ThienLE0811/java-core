package com.javacore.practice.basics;

import java.util.*;

/* Nhập lần lượt dữ liệu kiểu integer, double, String */
public class stdin_stdout_2 {
    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int i = sc.nextInt();
        double d = sc.nextDouble();
        sc.nextLine();
        String s = sc.nextLine();

        System.out.println("String: " + s);
        System.out.println("Double: " + d);
        System.out.println("Int: " + i);
        sc.close();
    }
}
