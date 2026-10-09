package com.javacore.t02_scope_operators_control_flow;

public class ControlFlowDemo {

    public static void main(String[] args) {
        // if - else if - else
        int score = 75;
        if (score >= 90) {
            System.out.println("Xếp loại: Giỏi");
        } else if (score >= 70) {
            System.out.println("Xếp loại: Khá");
        } else {
            System.out.println("Xếp loại: Trung bình");
        }

        // switch truyền thống: cần break, nếu quên sẽ bị "fall-through" (chạy tiếp xuống case dưới)
        int dayNumber = 3;
        switch (dayNumber) {
            case 1:
                System.out.println("Thứ Hai");
                break;
            case 2:
                System.out.println("Thứ Ba");
                break;
            case 3:
                System.out.println("Thứ Tư");
                break;
            default:
                System.out.println("Ngày khác");
        }

        // switch expression (Java 14+): dùng "->", không fall-through, trả về giá trị trực tiếp
        String dayName = switch (dayNumber) {
            case 1 -> "Thứ Hai";
            case 2 -> "Thứ Ba";
            case 3 -> "Thứ Tư";
            default -> "Ngày khác";
        };
        System.out.println("switch expression: " + dayName);

        // switch expression với yield khi cần nhiều dòng logic trong một case
        int quarterOfYear = switch (dayNumber) {
            case 1, 2, 3 -> {
                System.out.println("Đang tính quý cho ngày đầu tuần");
                yield 1;
            }
            default -> 2;
        };
        System.out.println("quarterOfYear: " + quarterOfYear);

        // for: dùng khi biết trước số lần lặp
        for (int i = 1; i <= 3; i++) {
            System.out.println("for loop, i = " + i);
        }

        // enhanced for (for-each): duyệt qua từng phần tử của mảng/collection
        int[] numbers = {10, 20, 30};
        for (int number : numbers) {
            System.out.println("for-each, number = " + number);
        }

        // while: kiểm tra điều kiện trước, có thể không chạy lần nào
        int count = 0;
        while (count < 3) {
            System.out.println("while loop, count = " + count);
            count++;
        }

        // do-while: chạy ít nhất một lần trước khi kiểm tra điều kiện
        int attempt = 0;
        do {
            System.out.println("do-while loop, attempt = " + attempt);
            attempt++;
        } while (attempt < 3);

        // break: thoát ngay khỏi vòng lặp
        for (int i = 0; i < 10; i++) {
            if (i == 3) {
                break; // dừng lặp khi i == 3
            }
            System.out.println("break demo, i = " + i);
        }

        // continue: bỏ qua vòng lặp hiện tại, tiếp tục vòng kế tiếp
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                continue; // bỏ qua số chẵn
            }
            System.out.println("continue demo, số lẻ i = " + i);
        }

        // Labeled break/continue: điều khiển vòng lặp lồng nhau
        outerLoop:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    break outerLoop; // thoát hẳn vòng lặp ngoài, không chỉ vòng trong
                }
                System.out.println("labeled loop, i = " + i + ", j = " + j);
            }
        }
    }
}
