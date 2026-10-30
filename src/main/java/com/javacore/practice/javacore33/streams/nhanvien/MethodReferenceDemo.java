package com.javacore.practice.javacore33.streams.nhanvien;

import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.*;

/**
 * Bốn kiểu method reference (::) - mỗi kiểu kèm lambda tương đương.
 * Cả bốn đều đã xuất hiện trong CollectorsNangCaoDemo của bài 27.
 */
public class MethodReferenceDemo {

    private static final List<Employee> NHAN_VIEN = EmployeeData.danhSach();

    private static final String DONG = "%-38s: %s%n";

    public static void main(String[] args) {
        kieu1_PhuongThucStatic();
        kieu2_PhuongThucCuaMotObjectCuThe();
        kieu3_PhuongThucCuaObjectBatKy();
        kieu4_Constructor();
        khiNaoKhongDungDuoc();
    }

    /**
     * Kiểu 1: Lop::phuongThucStatic
     */
    private static void kieu1_PhuongThucStatic() {
        System.out.println("=== Kiểu 1: Lop::phuongThucStatic ===");

        List<String> chuoiSo = List.of("10", "200", "3");

        List<Integer> bangLambda = chuoiSo.stream().map(s -> Integer.parseInt(s)).toList();
        List<Integer> bangMethodRef = chuoiSo.stream().map(Integer::parseInt).toList();

        System.out.printf(DONG, "s -> Integer.parseInt(s)", bangLambda);
        System.out.printf(DONG, "Integer::parseInt", bangMethodRef);
        System.out.printf(DONG, "Giống nhau?", bangLambda.equals(bangMethodRef));

        System.out.println();
    }

    /**
     * Kiểu 2: bien::phuongThuc - gọi trên MỘT object đã có sẵn.
     */
    private static void kieu2_PhuongThucCuaMotObjectCuThe() {
        System.out.println("=== Kiểu 2: bien::phuongThuc (một object cụ thể) ===");

        // System.out là một object kiểu PrintStream. Dù viết lambda hay ::,
        // println luôn được gọi trên đúng object System.out đó.
        Consumer<String> bangLambda = x -> System.out.println("    [lambda]     " + x);
        Consumer<String> bangMethodRef = System.out::println;

        bangLambda.accept("xin chào");
        System.out.print("    [method ref] ");
        bangMethodRef.accept("xin chào");

        // Một object bất kỳ khác cũng vậy, không riêng System.out:
        String tienTo = "NV-";
        Function<String, String> themTienTo = tienTo::concat;   // ten -> tienTo.concat(ten)
        System.out.printf(DONG, "tienTo::concat", themTienTo.apply("An"));

        System.out.println();
    }

    /**
     * Kiểu 3: Lop::phuongThucInstance - KIỂU KHÓ HIỂU NHẤT.
     */
    private static void kieu3_PhuongThucCuaObjectBatKy() {
        System.out.println("=== Kiểu 3: Lop::phuongThucInstance (object bất kỳ) ===");

        // Employee::name trông như gọi method static trên class Employee, nhưng name()
        // là method INSTANCE. Ý nghĩa thật: "nhận vào một Employee, gọi .name() trên nó".
        // Tham số đầu tiên của lambda trở thành NGƯỜI NHẬN lời gọi method.
        Function<Employee, String> bangLambda = e -> e.name();
        Function<Employee, String> bangMethodRef = Employee::name;

        Employee nguoiDauTien = NHAN_VIEN.get(0);
        System.out.printf(DONG, "e -> e.name()", bangLambda.apply(nguoiDauTien));
        System.out.printf(DONG, "Employee::name", bangMethodRef.apply(nguoiDauTien));

        // Nếu method có tham số thì tham số lambda thứ nhất là người nhận,
        // các tham số còn lại được truyền vào method.
        // (a, b) -> a.compareTo(b)  ==  String::compareTo
        Comparator<String> soSanhLambda = (a, b) -> a.compareTo(b);
        Comparator<String> soSanhMethodRef = String::compareTo;
        System.out.printf(DONG, "(a,b) -> a.compareTo(b)", soSanhLambda.compare("An", "Bảo"));
        System.out.printf(DONG, "String::compareTo", soSanhMethodRef.compare("An", "Bảo"));

        System.out.println();
    }

    /**
     * Kiểu 4: Lop::new
     */
    private static void kieu4_Constructor() {
        System.out.println("=== Kiểu 4: Lop::new (constructor) ===");

        // TreeMap::new trong bài 27 chính là () -> new TreeMap<>().
        // collect gọi supplier này để tạo map rỗng rồi đổ dữ liệu vào.
        Map<String, Long> bangLambda = NHAN_VIEN.stream().limit(3)
                .collect(toMap(Employee::name, Employee::salary, (cu, moi) -> moi, () -> new TreeMap<>()));
        Map<String, Long> bangMethodRef = NHAN_VIEN.stream().limit(3)
                .collect(toMap(Employee::name, Employee::salary, (cu, moi) -> moi, TreeMap::new));

        System.out.printf(DONG, "() -> new TreeMap<>()", bangLambda.keySet());
        System.out.printf(DONG, "TreeMap::new", bangMethodRef.keySet());

        // Constructor có tham số cũng được, miễn là khớp chữ ký của functional interface.
        Function<String, StringBuilder> taoBuilder = StringBuilder::new;  // s -> new StringBuilder(s)
        System.out.printf(DONG, "StringBuilder::new", taoBuilder.apply("abc").reverse());

        System.out.println();
    }

    /**
     * Khi nào KHÔNG viết được bằng ::
     */
    private static void khiNaoKhongDungDuoc() {
        System.out.println("=== Khi nào phải giữ lambda ===");

        // :: chỉ dùng được khi lambda CHỈ gọi đúng một method và truyền nguyên si tham số.
        // Hễ có thêm bất kỳ phép tính, so sánh hay literal nào -> phải giữ lambda.

        // e -> e.salary() > 20_000_000   : có phép so sánh -> KHÔNG có dạng ::
        long soNguoiLuongCao = NHAN_VIEN.stream().filter(e -> e.salary() > 20_000_000).count();
        System.out.printf(DONG, "filter(e -> e.salary() > 20tr)", soNguoiLuongCao);

        // e -> e.name().toUpperCase()    : hai lời gọi lồng nhau -> KHÔNG có dạng ::
        String tenHoa = NHAN_VIEN.get(0).name().toUpperCase();
        System.out.printf(DONG, "e -> e.name().toUpperCase()", tenHoa);

        // e -> e                         : viết được là Function.identity(), nhưng e -> e
        //                                  ngắn và rõ hơn, không cần đổi.
        System.out.printf(DONG, "e -> e  (hay Function.identity())", NHAN_VIEN.get(0).name());

        System.out.println();
    }
}
