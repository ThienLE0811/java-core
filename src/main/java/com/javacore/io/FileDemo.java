package com.javacore.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDemo {

    public static void main(String[] args) throws IOException {
        Path tempFile = Files.createTempFile("java-core", ".txt");
        Files.writeString(tempFile, "Hello from java-core!");

        String content = Files.readString(tempFile);
        System.out.println(content);

        Files.delete(tempFile);
    }
}
