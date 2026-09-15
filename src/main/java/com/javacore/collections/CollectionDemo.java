package com.javacore.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionDemo {

    public static void main(String[] args) {
        List<String> fruits = new ArrayList<>(List.of("apple", "banana", "cherry"));
        fruits.add("date");

        Map<String, Integer> fruitLengths = new HashMap<>();
        for (String fruit : fruits) {
            fruitLengths.put(fruit, fruit.length());
        }

        fruitLengths.forEach((fruit, length) -> System.out.println(fruit + " -> " + length));
    }
}
