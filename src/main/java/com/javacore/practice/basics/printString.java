package com.javacore.practice.basics;

import java.lang.*;
import java.io.IOException;

public class printString {
    public static void main(String[] args) {
        char ch = '\0';
        try {
            System.out.println("Hay nhap ky tu:");
            ch = (char) System.in.read();
        } catch (IOException e) {
            System.out.println("Nhập lỗi!");
        }
        System.out.println("Ky tu vua nhap:" + ch);
    }
}
