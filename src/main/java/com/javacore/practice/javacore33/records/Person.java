package com.javacore.practice.javacore33.records;

import com.javacore.practice.javacore33.streams.validator.EmailValidator;
import com.javacore.practice.javacore33.streams.validator.Validator;

import java.util.Locale;
import java.util.Objects;

public record Person(String name, int age, String email) {

    // Validator không giữ trạng thái -> một instance dùng chung cho mọi Person.
    private static final Validator<String> EMAIL = new EmailValidator();

    private static final int TUOI_TOI_DA = 150;
    private static final String EMAIL_MAC_DINH = "chua-dat@example.com";

    public Person {
        // null = lỗi lập trình -> NPE. Dữ liệu có mặt nhưng sai -> IllegalArgumentException.
        Objects.requireNonNull(name, "name không được null");
        name = name.strip();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name không được rỗng");
        }

        // int là primitive, không cần null check.
        if (age < 0 || age > TUOI_TOI_DA) {
            throw new IllegalArgumentException(
                    "age phải trong khoảng 0.." + TUOI_TOI_DA + ", nhận: " + age);
        }

        Objects.requireNonNull(email, "email không được null");
        // Chuẩn hoá TRƯỚC khi validate, nếu không là kiểm tra một chuỗi khác chuỗi được lưu.
        // Locale.ROOT: toLowerCase() không tham số dùng locale máy, "I" -> "ı" trên máy Thổ Nhĩ Kỳ.
        email = email.strip().toLowerCase(Locale.ROOT);
        if (!EMAIL.validate(email)) {
            throw new IllegalArgumentException("email không hợp lệ: " + email);
        }
    }

    public static Person cua(String name, int age) {
        return new Person(name, age, EMAIL_MAC_DINH);
    }

    public boolean laNguoiLon() {
        return age >= 18;
    }

    // Wither pattern: cách duy nhất "sửa" một immutable object là dựng cái mới.
    // Email mới vẫn được validate lại, vì mọi đường vào đều qua compact constructor.
    public Person doiEmail(String emailMoi) {
        return new Person(name, age, emailMoi);
    }
}
