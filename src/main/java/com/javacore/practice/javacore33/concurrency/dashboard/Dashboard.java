package com.javacore.practice.javacore33.concurrency.dashboard;

import com.javacore.practice.javacore33.concurrency.dashboard.DichVuGiaLap.DonHang;
import com.javacore.practice.javacore33.concurrency.dashboard.DichVuGiaLap.GoiY;
import com.javacore.practice.javacore33.concurrency.dashboard.DichVuGiaLap.NguoiDung;

import java.util.List;
import java.util.Objects;

public record Dashboard(NguoiDung nguoiDung, List<DonHang> donHang, List<GoiY> goiY) {

    public Dashboard {
        Objects.requireNonNull(nguoiDung, "nguoiDung không được null");
        Objects.requireNonNull(donHang, "donHang không được null");
        Objects.requireNonNull(goiY, "goiY không được null");

        // copyOf trong compact constructor: record chỉ bất biến ở tham chiếu,
        // không copy thì người gọi vẫn sửa được List bên ngoài.
        donHang = List.copyOf(donHang);
        goiY = List.copyOf(goiY);
    }

    public String moTaNgan() {
        return "%s | %d đơn | %d gợi ý".formatted(nguoiDung.ten(), donHang.size(), goiY.size());
    }
}
