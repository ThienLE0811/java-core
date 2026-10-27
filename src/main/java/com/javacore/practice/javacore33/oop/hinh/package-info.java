/**
 * Bài 9 - abstract class và đa hình: {@link com.javacore.practice.javacore33.oop.hinh.Hinh}
 * với ba lớp con, tính tổng diện tích từ một mảng {@code Hinh[]}.
 *
 * <p>Tách thành package riêng vì đây là một <em>cây kế thừa</em> gồm bốn class đi liền nhau,
 * khác với bài 7 và 8 - mỗi bài chỉ một class đứng độc lập trong {@code oop}.
 *
 * <p>Cả bốn tính chất OOP đều có mặt: đóng gói ({@code private final}, validate ở constructor,
 * không setter), trừu tượng ({@code abstract class Hinh} giấu công thức sau một chữ ký),
 * kế thừa ({@code extends} và {@code kiemTraKichThuoc} dùng chung), đa hình
 * ({@code danhSach[i].dienTich()} chạy đúng với mọi loại hình, kể cả loại thêm sau này).
 */
package com.javacore.practice.javacore33.oop.hinh;
