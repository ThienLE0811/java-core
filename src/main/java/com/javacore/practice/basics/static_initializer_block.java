package com.javacore.practice.basics;

import java.util.Scanner;

public class static_initializer_block {

    static int B;
    static int H;
    static boolean flag;

    // (2) STATIC BLOCK: chay 1 lan khi class duoc nap, TRUOC main.
    static {
        Scanner scanner = new Scanner(System.in);

        B = scanner.nextInt();
        H = scanner.nextInt();
        flag = B > 0 && H > 0;
        scanner.close();
    }

    public static void main(String[] args) {
        if (flag) {
            int acreage = B * H;
            System.out.print(acreage);
        } else {
            System.out.println(new Exception("Breadth and height must be positive"));

        }
    }
}
