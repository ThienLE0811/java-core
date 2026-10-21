package com.javacore.practice.basics;

/**
 * Bài luyện 1 - Thống kê trên mảng số nguyên.
 *
 * <p>Tìm max thì ai cũng viết được. Phần đáng luyện là ba quyết định quanh nó, đều là chỗ
 * người từ JS/TS sang hay bỏ qua:
 * <ol>
 *   <li><b>Hợp đồng khi input sai</b> - trả về giá trị mặc định hay ném exception? JS thường
 *       trả {@code undefined}/{@code -Infinity} và lỗi lộ ra ở chỗ khác. Java nên ném sớm.</li>
 *   <li><b>Giá trị khởi tạo của biến tích luỹ</b> - xem comment trong {@link #max(int[])}.</li>
 *   <li><b>Mỗi nhánh {@code if} ở đây = một test ở {@code NumberStatsTest}.</b></li>
 * </ol>
 */
public final class NumberStats {

    private NumberStats() {
        // Lớp tiện ích chỉ chứa static method: chặn khởi tạo để không ai gọi "new NumberStats()".
    }

    /**
     * Trả về giá trị lớn nhất trong mảng.
     *
     * @param numbers mảng đầu vào; không được null và không được rỗng
     * @return phần tử lớn nhất của {@code numbers}
     * @throws IllegalArgumentException nếu {@code numbers} là null hoặc rỗng
     */
    public static int max(int[] numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("numbers must not be null");
        }
        if (numbers.length == 0) {
            // Mảng rỗng KHÔNG có max. Trả về 0 hay Integer.MIN_VALUE đều là nói dối với caller,
            // vì đó là giá trị không tồn tại trong mảng. Ném để lỗi lộ ra ngay tại nguồn.
            throw new IllegalArgumentException("numbers must not be empty");
        }

        // Khởi tạo bằng PHẦN TỬ ĐẦU TIÊN, không phải 0.
        // Nếu khởi tạo = 0 thì mảng toàn số âm {-5, -1, -9} sẽ trả về 0 - sai, và sai âm thầm.
        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }
        return max;
    }
}
