package com.javacore.practice.javacore33.streams.validator;

import java.util.Objects;

public class EmailValidator implements Validator<String> {

    @Override
    public boolean validate(String value) {
        Objects.requireNonNull(value, "email không được null");

        int viTriAt = value.indexOf('@');
        // <= 0 gộp hai ca: không có '@' (-1), và '@' đứng đầu nên phần tên rỗng (0).
        if (viTriAt <= 0 || viTriAt != value.lastIndexOf('@')) {
            return false;
        }

        String ten = value.substring(0, viTriAt);
        String mien = value.substring(viTriAt + 1);
        if (ten.isBlank() || mien.isBlank()) {
            return false;
        }

        // Dấu chấm phải nằm giữa tên miền: "a@.com" và "a@b." đều hỏng.
        int viTriCham = mien.indexOf('.');
        return viTriCham > 0 && viTriCham < mien.length() - 1;
    }
}
