package com.javacore.fundamentals.t02_scope_operators_control_flow;

public class ScopeDemo {

    // Instance field: scope là toàn bộ object, tồn tại theo vòng đời của instance
    private int instanceCounter = 0;

    // Static field: scope là toàn bộ class, dùng chung cho mọi instance
    private static int staticCounter = 0;

    public static void main(String[] args) {
        // Local variable: scope chỉ trong khối lệnh (block) chứa nó, từ chỗ khai báo tới hết block
        int localValue = 10;
        System.out.println("localValue trong main: " + localValue);

        {
            // Block scope: biến khai báo trong { } chỉ tồn tại trong block đó
            int blockValue = 20;
            System.out.println("blockValue trong block: " + blockValue);
        }
        // System.out.println(blockValue); // LỖI compile: blockValue đã hết scope

        // Vòng lặp for: biến i chỉ tồn tại trong scope của vòng lặp
        for (int i = 0; i < 3; i++) {
            int doubled = i * 2; // doubled được tạo mới mỗi vòng lặp, scope trong thân for
            System.out.println("i = " + i + ", doubled = " + doubled);
        }
        // System.out.println(i); // LỖI compile: i đã hết scope

        // Shadowing: biến local có thể trùng tên với field, biến local sẽ "che" field
        ScopeDemo demo = new ScopeDemo();
        demo.demonstrateShadowing();

        // Static field dùng chung cho mọi instance
        ScopeDemo demo1 = new ScopeDemo();
        ScopeDemo demo2 = new ScopeDemo();
        demo1.incrementCounters();
        demo2.incrementCounters();
        System.out.println("instanceCounter demo1: " + demo1.instanceCounter); // riêng từng instance
        System.out.println("instanceCounter demo2: " + demo2.instanceCounter); // riêng từng instance
        System.out.println("staticCounter (dùng chung): " + staticCounter);     // cộng dồn chung
    }

    private int instanceValue = 100;

    private void demonstrateShadowing() {
        int instanceValue = 5; // biến local "che" field cùng tên
        System.out.println("Local instanceValue (shadowing): " + instanceValue);
        System.out.println("Field instanceValue (dùng this): " + this.instanceValue);
    }

    private void incrementCounters() {
        instanceCounter++;
        staticCounter++;
    }
}
