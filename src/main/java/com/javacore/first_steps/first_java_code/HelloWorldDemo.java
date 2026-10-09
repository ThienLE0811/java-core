package com.javacore.first_steps.first_java_code;

public class HelloWorldDemo {

    public static void main(String[] args) {
        IO.println("Hello world!");
        var name = IO.readln("What is your name?");
        IO.println("Hello " + name);
    }
}

