package com.javacore.practice.javacore33.streams.optional;

/**
 * Bài 28 - Địa chỉ, quan hệ 1-1 với User qua userId. Không phải user nào cũng có
 * địa chỉ: đó là lý do AddressRepository trả Optional và ta cần flatMap.
 */
public record Address(long userId, String street, String city) {

    @Override
    public String toString() {
        return street + ", " + city;
    }
}
