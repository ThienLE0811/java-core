package com.javacore.practice.javacore33.projects.banhang.model;

import java.util.Objects;

public record Customer(String id, String ten, String email) {

    public Customer {
        Objects.requireNonNull(id, "Mã khách hàng không được null");
        Objects.requireNonNull(ten, "Tên khách hàng không được null");
        Objects.requireNonNull(email, "Email không được null");
        if (id.isBlank() || ten.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException("Mã, tên và email khách hàng không được trống");
        }
    }
}
