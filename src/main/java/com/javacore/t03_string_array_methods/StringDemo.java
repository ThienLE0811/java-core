package com.javacore.t03_string_array_methods;

public class StringDemo {

    public static void main(String[] args) {
        // String literal: được lưu trong String Pool, tái sử dụng nếu nội dung giống nhau
        String s1 = "hello";
        String s2 = "hello";
        System.out.println("s1 == s2 (cùng literal, cùng reference trong pool): " + (s1 == s2)); // true

        // new String(...): luôn tạo object mới trên heap, không nằm trong pool
        String s3 = new String("hello");
        System.out.println("s1 == s3 (new String, khác reference): " + (s1 == s3)); // false
        System.out.println("s1.equals(s3) (so sánh nội dung): " + s1.equals(s3));   // true

        // String immutable: mọi thao tác "chỉnh sửa" đều trả về object mới, không đổi object cũ
        String original = "abc";
        String upper = original.toUpperCase();
        System.out.println("original không đổi: " + original + ", upper là object mới: " + upper);

        // Nối chuỗi bằng + trong vòng lặp tạo nhiều object trung gian, tốn kém
        String result = "";
        for (int i = 0; i < 3; i++) {
            result += i; // mỗi lần += tạo 1 String mới
        }
        System.out.println("Nối chuỗi bằng +: " + result);

        // StringBuilder: mutable, nên nối chuỗi nhiều lần trong vòng lặp nên dùng cái này
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(i);
        }
        System.out.println("Nối chuỗi bằng StringBuilder: " + sb);

        // Các method thường dùng
        String text = "  Hello, Java World!  ";
        System.out.println("length(): " + text.length());
        System.out.println("trim(): [" + text.trim() + "]");
        System.out.println("strip() (Unicode-aware, Java 11+): [" + text.strip() + "]");
        System.out.println("toLowerCase(): " + text.toLowerCase());
        System.out.println("charAt(2): " + text.charAt(2));
        System.out.println("indexOf(\"Java\"): " + text.indexOf("Java"));
        System.out.println("substring(2, 7): [" + text.substring(2, 7) + "]"); // từ index 2 đến trước index 7
        System.out.println("replace(\"Java\", \"JS\"): " + text.replace("Java", "JS"));
        System.out.println("contains(\"World\"): " + text.contains("World"));
        System.out.println("split(\",\"): " + java.util.Arrays.toString(text.trim().split(",")));

        // So sánh chuỗi
        System.out.println("\"abc\".compareTo(\"abd\"): " + "abc".compareTo("abd")); // âm vì 'c' < 'd'
        System.out.println("\"abc\".equalsIgnoreCase(\"ABC\"): " + "abc".equalsIgnoreCase("ABC"));

        // String.format và text block (Java 15+)
        String formatted = String.format("Tên: %s, Tuổi: %d", "An", 25);
        System.out.println(formatted);

        String textBlock = """
                Dòng 1
                Dòng 2
                """; // text block: giữ nguyên định dạng nhiều dòng, không cần nối \n thủ công
        System.out.print(textBlock);

        // isEmpty vs isBlank
        System.out.println("\"\".isEmpty(): " + "".isEmpty());
        System.out.println("\"   \".isBlank() (Java 11+, coi khoảng trắng là rỗng): " + "   ".isBlank());
    }
}
