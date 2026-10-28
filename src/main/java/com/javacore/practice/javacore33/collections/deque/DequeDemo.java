package com.javacore.practice.javacore33.collections.deque;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/**
 * Demo bài 23 - {@link ArrayDeque}: Lịch sử trình duyệt và so sánh với {@link Stack}.
 */
public class DequeDemo {

    public static void main(String[] args) {
        demoLichSuTrinhDuyet();
        soSanhArrayDequeVsStack();
    }

    private static void demoLichSuTrinhDuyet() {
        System.out.println("=== 1. Mô phỏng lịch sử duyệt web ===");

        // Khởi động trình duyệt tại google.com
        BrowserHistory browser = new BrowserHistory("google.com");
        System.out.println("Khởi động: " + browser.printHistory());

        // Người dùng lướt web tiếp:
        browser.visit("dev.java");
        System.out.println("Visit dev.java:      " + browser.printHistory());

        browser.visit("github.com");
        System.out.println("Visit github.com:    " + browser.printHistory());

        browser.visit("stackoverflow.com");
        System.out.println("Visit stackoverflow: " + browser.printHistory());

        // 1. Thử Back 2 lần
        System.out.println("\nBấm Back lần 1: " + browser.back().orElse(""));
        System.out.println("  Trạng thái: " + browser.printHistory());

        System.out.println("Bấm Back lần 2: " + browser.back().orElse(""));
        System.out.println("  Trạng thái: " + browser.printHistory());

        // 2. Thử Forward 1 lần
        System.out.println("\nBấm Forward lần 1: " + browser.forward().orElse(""));
        System.out.println("  Trạng thái: " + browser.printHistory());

        // 3. EDGE CASE MẤU CHỐT: Khi đang đứng ở dev.java, người dùng mở link MỚI (youtube.com)
        // -> Lịch sử forward (stackoverflow.com) PHẢI BỊ XÓA SẠCH!
        System.out.println("\n=== EDGE CASE: Visit trang mới sau khi đã Back ===");
        System.out.println("Trước khi visit mới: Forward stack có: " + browser.getForwardHistory());

        browser.visit("youtube.com");
        System.out.println("Sau khi visit youtube.com:");
        System.out.println("  Trạng thái: " + browser.printHistory());
        System.out.println("  canForward? " + browser.canForward() + " (Forward stack đã bị xóa sạch!)");

        // 4. EDGE CASE: Back liên tiếp về tận trang đầu rồi back tiếp
        System.out.println("\n=== EDGE CASE: Back khi đang ở trang đầu tiên ===");
        while (browser.canBack()) {
            browser.back();
        }
        System.out.println("Đã lùi về trang đầu tiên: " + browser.getCurrentPage());
        System.out.println("Thử back tiếp khi canBack = false:");
        var kqBack = browser.back();
        System.out.println("  Kết quả: " + kqBack + " (trả về Optional.empty, không ném exception)");
        System.out.println("  Trang hiện tại vẫn là: " + browser.getCurrentPage());
    }

    private static void soSanhArrayDequeVsStack() {
        System.out.println();
        System.out.println("=== 2. Tại sao Java hiện đại dùng ArrayDeque thay cho Stack? ===");

        // Stack là class cũ thừa kế từ Vector (Java 1.0 - 1996)
        Stack<String> stackCu = new Stack<>();
        stackCu.push("A");
        stackCu.push("B");
        // Điểm yếu 1 của Stack cũ: Cho phép chèn vào giữa danh sách, phá vỡ nguyên tắc LIFO!
        stackCu.add(0, "Pha_Vo_LIFO");
        System.out.println("Stack cũ (Vector): " + stackCu + "  <- có thể add(0, ...) phá vỡ LIFO");

        // ArrayDeque là chuẩn hiện đại (Java 6+)
        Deque<String> dequeMoi = new ArrayDeque<>();
        dequeMoi.push("A");
        dequeMoi.push("B");
        System.out.println("ArrayDeque mới:    " + dequeMoi);

        System.out.println("-> Lý do chọn ArrayDeque:");
        System.out.println("  1. Không có synchronized vô ích như Stack -> Nhanh hơn vượt trội.");
        System.out.println("  2. Cài đặt bằng mảng tròn liên tục (Circular Array) -> Thân thiện CPU cache.");
        System.out.println("  3. Tuân thủ nghiêm ngặt giao diện Deque (chỉ thao tác ở 2 đầu).");
    }
}
