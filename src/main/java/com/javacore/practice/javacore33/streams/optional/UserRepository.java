package com.javacore.practice.javacore33.streams.optional;

import java.util.List;
import java.util.Optional;

/**
 * Bài 28 - Repository giả lập, mô phỏng đúng chữ ký Spring Data JPA:
 * {@code Optional<User> findByEmail(String email)}.
 * Spring Data sinh sẵn hàm này khi bạn khai báo nó trong interface repository.
 */
public final class UserRepository {

    private static final List<User> DU_LIEU = List.of(
            new User(1, "Nguyễn Văn An", "an.nguyen@example.com", "0901234567", true),
            new User(2, "Trần Minh Bảo", "bao.tran@example.com", null, true),          // chưa có phone
            new User(3, "Lê Thu Cúc", "cuc.le@example.com", "0912345678", false),      // đã khoá
            new User(4, "Phạm Quốc Dũng", "dung.pham@example.com", null, false)        // vừa null phone vừa khoá
    );

    /**
     * Trả Optional.empty() khi không tìm thấy - KHÔNG BAO GIỜ trả null.
     * Một hàm khai báo trả Optional mà return null là lỗi nặng nhất quanh Optional:
     * người gọi sẽ viết .map() rồi ăn NullPointerException ngay tại chỗ gọi.
     */
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return DU_LIEU.stream()
                .filter(u -> u.email().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<User> findById(long id) {
        return DU_LIEU.stream().filter(u -> u.id() == id).findFirst();
    }

    public List<User> findAll() {
        return DU_LIEU;
    }
}
