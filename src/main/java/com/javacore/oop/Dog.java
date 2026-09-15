package com.javacore.oop;

public class Dog extends Animal {

    public Dog(String name) {
        super(name);
    }

    @Override
    public String makeSound() {
        return getName() + " says: Woof!";
    }
}
