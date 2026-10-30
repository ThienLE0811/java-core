package com.javacore.practice.javacore33.streams.optional;

import java.util.Objects;

/**
 * Bài 28 - Người dùng. phone CỐ Ý cho phép null để thấy map() tự nuốt null
 * thành Optional rỗng; active dùng cho filter().
 */
public record User(long id, String name, String email, String phone, boolean active) {

    public User {
        if (id <= 0) {
            throw new IllegalArgumentException("id phải dương: " + id);
        }
        Objects.requireNonNull(name, "Tên không được null");
        Objects.requireNonNull(email, "Email không được null");
        // phone KHÔNG requireNonNull: null ở đây là hợp lệ, nghĩa là "chưa khai báo".
        // Đừng bọc nó thành Optional<String> field - lý do xem OptionalDemo mục 8.
    }

    @Override
    public String toString() {
        return String.format("#%d %-16s | %-22s | %-12s | %s",
                id, name, email, phone == null ? "(chưa có)" : phone,
                active ? "đang hoạt động" : "đã khoá");
    }
}
