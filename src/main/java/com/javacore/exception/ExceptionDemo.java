package com.javacore.exception;

public class ExceptionDemo {

    public static void main(String[] args) {
        try {
            divide(10, 0);
        } catch (ArithmeticException e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }

    private static int divide(int a, int b) {
        return a / b;
    }
}
