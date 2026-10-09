package com.javacore.language.constructs.basics;

/**
 * Creating Arrays in Your Programs
 * <p>
 * Khái niệm: array là container chứa số lượng cố định các giá trị cùng 1 kiểu,
 * truy cập qua index bắt đầu từ 0.
 * <p>
 * 1. Declaring a Variable to Refer to an Array
 * - Cú pháp: type[] name; convention đặt [] ngay sau kiểu dữ liệu, không phải sau tên biến.
 * <p>
 * 2. Creating, Initializing, and Accessing an Array
 * - Tạo bằng new: anArray = new int[10]; cấp phát 10 phần tử, index 0..9.
 * - Khởi tạo nhanh (initializer): int[] anArray = {100, 200, 300}; không cần new,
 * độ dài tự suy ra từ số phần tử liệt kê.
 * <p>
 * 3. Creating Multidimensional Arrays
 * - type[][] name; Java không có mảng 2D thật (vùng nhớ liên tục như C/Fortran),
 * mà là "mảng của mảng tham chiếu" (array of arrays).
 * - Hệ quả: có thể tạo jagged array - các hàng độ dài khác nhau,
 * vì mỗi hàng là 1 mảng con độc lập (xem displayBidimensionalArray bên dưới).
 * <p>
 * 4. Using the Length of an Array
 * - length là thuộc tính (không phải method, không có dấu ngoặc): anArray.length.
 * - Mảng nhiều chiều: phải gọi length từng cấp - arr.length là số hàng,
 * arr[i].length là số phần tử của hàng i (vì jagged nên mỗi hàng length khác nhau).
 * <p>
 * 5. Copying Arrays
 * - System.arraycopy() hoặc Arrays.copyOfRange(from, to) - to là exclusive.
 * <p>
 * 6. Array Manipulations - các method tiện ích của java.util.Arrays:
 * binarySearch(), equals(), fill(), sort(), parallelSort(), stream(), toString().
 * <p>
 * 7. Giá trị mặc định
 * - Mảng là field: compiler tự gán giá trị mặc định cho từng phần tử.
 * - Mảng là local variable: không tự gán mặc định, phải tự khởi tạo,
 * nếu không sẽ lỗi compile khi dùng tới.
 * <p>
 * Lưu ý: class đặt tên Arrays sẽ đụng tên với java.util.Arrays (class chứa
 * sort/fill/copyOfRange ở mục 6) - nếu cần import java.util.Arrays trong file này
 * phải dùng fully-qualified name java.util.Arrays.sort(...) thay vì import thẳng.
 */
public class Arrays {

    void displayBidimensionalArray(String[][] strings) {
        for (int arrayIndex = 0; arrayIndex < strings.length; arrayIndex++) {
            for (int index = 0; index < strings[arrayIndex].length; index++) {
                IO.print(strings[arrayIndex][index] + " ");
            }
            IO.println();
        }
    }


    void main() {
        String[][] strings = {
                {"one"},
                {"Maria", "Jennifer", "Patricia"},
                {"James", "Michael"},
                {"Washington", "London", "Paris", "Berlin", "Tokyo"}
        };

        IO.println("strings[0] " + strings[0][0]);
        displayBidimensionalArray(strings);
    }
}
