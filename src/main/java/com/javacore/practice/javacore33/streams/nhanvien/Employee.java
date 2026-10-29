package com.javacore.practice.javacore33.streams.nhanvien;

import java.util.Comparator;
import java.util.Objects;

/**
 * Bài 26 - Nhân viên: dữ liệu đầu vào cho các bài tập Stream với Object.
 * salary tính bằng đồng (18_500_000 = 18,5 triệu).
 */
public record Employee(String name, Department department, long salary, int yearsOfExperience) {

    public static final Comparator<Employee> LUONG_GIAM_DAN = Comparator
            .comparingLong(Employee::salary).reversed()
            .thenComparing(Employee::name);

    public Employee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }
        Objects.requireNonNull(department, "Phòng ban không được null");
        if (salary < 0) {
            throw new IllegalArgumentException("Lương không được âm: " + salary);
        }
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Số năm kinh nghiệm không được âm: " + yearsOfExperience);
        }
    }

    @Override
    public String toString() {
        return String.format("%-18s | %-10s | %,13d | %2d năm",
                name, department.tenHienThi(), salary, yearsOfExperience);
    }
}
