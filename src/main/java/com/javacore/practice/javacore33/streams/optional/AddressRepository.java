package com.javacore.practice.javacore33.streams.optional;

import java.util.List;
import java.util.Optional;

/**
 * Bài 28 - Repository thứ hai. Có nó mới demo được flatMap: tra user ra Optional,
 * rồi từ user tra tiếp địa chỉ cũng ra Optional -> hai tầng Optional lồng nhau.
 */
public final class AddressRepository {

    private static final List<Address> DU_LIEU = List.of(
            new Address(1, "12 Nguyễn Huệ", "Hồ Chí Minh"),
            new Address(3, "45 Tràng Tiền", "Hà Nội")
            // user 2 và 4 KHÔNG có địa chỉ -> findByUserId trả empty
    );

    public Optional<Address> findByUserId(long userId) {
        return DU_LIEU.stream().filter(a -> a.userId() == userId).findFirst();
    }
}
