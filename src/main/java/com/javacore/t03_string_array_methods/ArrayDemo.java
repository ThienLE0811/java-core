package com.javacore.t03_string_array_methods;

import java.util.Arrays;

public class ArrayDemo {

    public static void main(String[] args) {
        // Khai báo mảng: kích thước CỐ ĐỊNH ngay khi tạo, khác với JS array có thể tự co giãn
        int[] numbers = new int[5]; // mặc định các phần tử int = 0
        System.out.println("Mảng mặc định: " + Arrays.toString(numbers));

        // Khởi tạo có giá trị luôn
        int[] scores = {90, 85, 70, 60, 95};
        System.out.println("scores: " + Arrays.toString(scores));

        // Truy cập / gán theo index, giống JS
        scores[0] = 100;
        System.out.println("scores sau khi sửa index 0: " + Arrays.toString(scores));

        // Truy cập ngoài phạm vi -> ArrayIndexOutOfBoundsException lúc runtime (JS chỉ trả về undefined)
        try {
            int outOfBounds = scores[10];
            System.out.println(outOfBounds);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Lỗi truy cập ngoài phạm vi: " + e.getMessage());
        }

        // length là field, không phải method (khác String.length())
        System.out.println("scores.length: " + scores.length);

        // Mảng đa chiều
        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6}
        };
        System.out.println("matrix[1][2]: " + matrix[1][2]); // 6
        System.out.println("matrix dạng đầy đủ: " + Arrays.deepToString(matrix));

        // Duyệt mảng: for thường và for-each
        for (int i = 0; i < scores.length; i++) {
            System.out.print(scores[i] + " ");
        }
        System.out.println();
        for (int score : scores) {
            System.out.print(score + " ");
        }
        System.out.println();

        // Mảng object: chứa reference, mặc định null nếu chưa gán
        String[] names = new String[3];
        System.out.println("names[0] mặc định: " + names[0]); // null
        names[0] = "An";
        System.out.println("names sau khi gán: " + Arrays.toString(names));

        // Các tiện ích của java.util.Arrays
        int[] unsorted = {5, 3, 8, 1, 9};
        int[] sorted = Arrays.copyOf(unsorted, unsorted.length); // copy trước để không đổi mảng gốc
        Arrays.sort(sorted);
        System.out.println("unsorted (không đổi): " + Arrays.toString(unsorted));
        System.out.println("sorted: " + Arrays.toString(sorted));

        int index = Arrays.binarySearch(sorted, 8); // chỉ dùng đúng khi mảng đã sort
        System.out.println("binarySearch tìm 8 trong sorted: index = " + index);

        int[] filled = new int[4];
        Arrays.fill(filled, 7);
        System.out.println("Arrays.fill(7): " + Arrays.toString(filled));

        int[] extended = Arrays.copyOf(unsorted, 8); // mở rộng, phần dư điền giá trị mặc định (0)
        System.out.println("copyOf mở rộng size 8: " + Arrays.toString(extended));

        boolean isEqual = Arrays.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3});
        System.out.println("Arrays.equals so sánh nội dung: " + isEqual); // true, khác == so sánh reference

        // Mảng có kích thước cố định -> muốn "thêm/xóa phần tử" linh hoạt như JS push/splice
        // thì nên dùng ArrayList (java.util.List) thay vì mảng thuần
    }
}
