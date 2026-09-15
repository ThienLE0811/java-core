package com.javacore.generics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoxTest {

    @Test
    void setAndGetReturnsSameValue() {
        Box<String> box = new Box<>();
        box.set("hello");

        assertEquals("hello", box.get());
    }
}
