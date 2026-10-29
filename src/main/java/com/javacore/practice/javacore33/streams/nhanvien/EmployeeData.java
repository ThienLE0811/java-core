package com.javacore.practice.javacore33.streams.nhanvien;

import java.util.List;

/**
 * Dữ liệu mẫu 24 nhân viên cho bài 26.
 * Cố ý có các ca biên: lương cao nhưng ít kinh nghiệm, kinh nghiệm nhiều nhưng
 * lương thấp, hai người trùng lương, và một phòng chỉ có 2 người.
 */
public final class EmployeeData {

    private static final List<Employee> NHAN_VIEN = List.of(
            // Kỹ thuật - 8 người
            new Employee("Nguyễn Văn An", Department.KY_THUAT, 32_000_000, 8),
            new Employee("Trần Minh Bảo", Department.KY_THUAT, 28_500_000, 6),
            new Employee("Lê Hoàng Cường", Department.KY_THUAT, 22_000_000, 4),
            new Employee("Phạm Thu Dung", Department.KY_THUAT, 18_000_000, 3),
            new Employee("Vũ Đức Dũng", Department.KY_THUAT, 16_500_000, 2),   // lương cao, chưa đủ 3 năm
            new Employee("Đỗ Thanh Giang", Department.KY_THUAT, 14_000_000, 7), // nhiều kinh nghiệm, lương thấp
            new Employee("Bùi Quang Huy", Department.KY_THUAT, 12_000_000, 1),
            new Employee("Hoàng Mỹ Hạnh", Department.KY_THUAT, 25_000_000, 5),

            // Kinh doanh - 6 người
            new Employee("Ngô Thị Lan", Department.KINH_DOANH, 30_000_000, 9),
            new Employee("Đặng Văn Long", Department.KINH_DOANH, 21_500_000, 5),
            new Employee("Trương Mỹ Linh", Department.KINH_DOANH, 17_000_000, 3),
            new Employee("Phan Tuấn Minh", Department.KINH_DOANH, 15_000_000, 4),  // đúng 15tr, KHÔNG "trên 15tr"
            new Employee("Lý Thu Nga", Department.KINH_DOANH, 13_500_000, 2),
            new Employee("Cao Văn Nam", Department.KINH_DOANH, 11_000_000, 1),

            // Marketing - 4 người
            new Employee("Hồ Ngọc Oanh", Department.MARKETING, 24_000_000, 6),
            new Employee("Dương Chí Phúc", Department.MARKETING, 19_000_000, 4),
            new Employee("Võ Thanh Quân", Department.MARKETING, 15_500_000, 3),
            new Employee("Tạ Mai Quỳnh", Department.MARKETING, 12_500_000, 2),

            // Nhân sự - 4 người
            new Employee("Chu Thị Sương", Department.NHAN_SU, 20_000_000, 7),
            new Employee("Mai Văn Sơn", Department.NHAN_SU, 16_000_000, 5),
            new Employee("Đinh Thu Thảo", Department.NHAN_SU, 13_000_000, 3),
            new Employee("Lưu Bá Tùng", Department.NHAN_SU, 10_500_000, 1),

            // Kế toán - 2 người
            new Employee("Nguyễn Thị Uyên", Department.KE_TOAN, 28_500_000, 10), // trùng lương với Trần Minh Bảo
            new Employee("Trịnh Văn Vinh", Department.KE_TOAN, 14_500_000, 2)
    );

    private EmployeeData() {
    }

    public static List<Employee> danhSach() {
        return NHAN_VIEN;
    }
}
