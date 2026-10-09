package com.javacore.t06_final_enum_record_nested_class_object_lifecycle;

public class NestedClassDemo {

    private int outerState = 100;

    public static void main(String[] args) {
        System.out.println("=== Static nested class: không cần instance của outer, dùng như 1 class độc lập ===");
        StaticCounter counter = new StaticCounter();
        counter.increment();
        counter.increment();
        System.out.println("StaticCounter.count = " + counter.getCount());

        System.out.println("\n=== Inner class (non-static): BẮT BUỘC phải có instance outer, giữ reference ngầm tới outer ===");
        NestedClassDemo outer = new NestedClassDemo();
        NestedClassDemo.InnerViewer viewer = outer.new InnerViewer(); // cú pháp đặc trưng: outer.new Inner()
        viewer.printOuterState();
        outer.outerState = 999; // đổi state của outer
        viewer.printOuterState(); // inner tự thấy giá trị mới, vì nó không giữ bản copy mà giữ reference tới outer

        System.out.println("\n=== Local class: khai báo bên trong method, chỉ sống và dùng được trong method đó ===");
        outer.demoLocalClass(5);

        System.out.println("\n=== Anonymous class: tạo object + định nghĩa (hoặc override) luôn tại chỗ dùng, không cần đặt tên class ===");
        outer.demoAnonymousClass();
    }

    // Static nested class: về bản chất là 1 top-level class được "gói" vào trong cho gọn namespace,
    // KHÔNG giữ reference tới outer instance -> tạo được mà không cần new NestedClassDemo() trước
    static class StaticCounter {
        private int count;

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }
    }

    // Inner class (không có static): mỗi instance của InnerViewer luôn gắn với đúng 1 instance NestedClassDemo cụ thể,
    // nên truy cập trực tiếp được field/method của outer (outerState) mà không cần truyền tham số
    class InnerViewer {
        void printOuterState() {
            System.out.println("outerState hiện tại (nhìn từ inner class) = " + outerState);
        }
    }

    void demoLocalClass(int threshold) {
        // Local class: định nghĩa ngay trong method, chỉ dùng được từ điểm khai báo tới hết method này,
        // vẫn "đóng" (capture) được biến effectively final của method chứa nó (threshold)
        class ThresholdChecker {
            boolean isOver(int value) {
                return value > threshold;
            }
        }

        ThresholdChecker checker = new ThresholdChecker();
        System.out.println("6 > threshold(" + threshold + ")? " + checker.isOver(6));
    }

    void demoAnonymousClass() {
        // Anonymous class: vừa tạo instance vừa viết luôn thân class (implement Runnable) tại chỗ gọi,
        // hữu ích khi chỉ dùng 1 lần và không cần đặt tên riêng cho class đó
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous class đang chạy, vẫn đọc được outerState = " + outerState);
            }
        };
        task.run();
    }
}
