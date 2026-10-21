package com.javacore.practice.basics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mẫu test cho package {@code practice} - mức khởi đầu.
 *
 * <p>Cả file chỉ dùng 3 thứ: {@code @Test}, {@code assertEquals}, {@code assertThrows}.
 * Chưa cần {@code @Nested}, {@code @ParameterizedTest} hay {@code @DisplayName} - những cái
 * đó thêm sau, khi gặp đúng vấn đề mà chúng giải quyết.
 *
 * <p>Quy tắc duy nhất cần nhớ lúc này: <b>mỗi nhánh quyết định trong code = một method test</b>.
 * {@code NumberStats.max} có 2 nhánh ném lỗi + 1 thân vòng lặp, nên ở đây có 5 test.
 */
class NumberStatsTest {

    @Test
    void max_should_returnLargestElement_when_arrayHasManyElements() {
        // Arrange: chuẩn bị input
        int[] numbers = {3, 9, 4};

        // Act: gọi method cần test
        int actual = NumberStats.max(numbers);

        // Assert: kiểm tra kết quả mong đợi
        assertEquals(9, actual);
    }

    @Test
    void max_should_returnLargestElement_when_allNumbersAreNegative() {
        // Ca quan trọng nhất của bài này: nếu trong NumberStats đổi
        // "int max = numbers[0]" thành "int max = 0" thì test này đỏ.
        int[] numbers = {-5, -1, -9};

        int actual = NumberStats.max(numbers);

        assertEquals(-1, actual);
    }

    @Test
    void max_should_returnThatElement_when_arrayHasSingleElement() {
        int[] numbers = {7};

        int actual = NumberStats.max(numbers);

        assertEquals(7, actual);
    }

    @Test
    void max_should_throwException_when_arrayIsNull() {
        // assertThrows nhận một lambda: "chạy đoạn này, tôi mong nó ném ra lỗi loại kia".
        // Không bọc trong lambda thì exception văng ra ngay và test đỏ.
        assertThrows(IllegalArgumentException.class, () -> NumberStats.max(null));
    }

    @Test
    void max_should_throwException_when_arrayIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> NumberStats.max(new int[0]));
    }
}
