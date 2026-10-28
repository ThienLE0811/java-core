package com.javacore.practice.javacore33.records;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecordDemo {

    public static void main(String[] args) {
        compilerSinhGi();
        khongSuaDuoc();
        validateTaiBien();
        chuanHoaDoiNghiaEquals();
        batBaiNong();
        bayDouble();
    }

    private static void compilerSinhGi() {
        System.out.println("=== 1. Compiler sinh sẵn những gì ===");

        Person p = new Person("Thiên", 30, "thienlv@paraline.io");

        // toString tự sinh: tên record + từng component. Không phải Person@1b6d3586.
        System.out.println("  toString  : " + p);

        // Accessor là name(), KHÔNG phải getName(). Jackson 2.12+ đọc được dạng này.
        System.out.println("  accessor  : " + p.name() + " / " + p.age() + " / " + p.email());

        // equals/hashCode theo GIÁ TRỊ, không theo tham chiếu.
        Person p2 = new Person("Thiên", 30, "thienlv@paraline.io");
        System.out.println("  p == p2   : " + (p == p2));
        System.out.println("  p.equals  : " + p.equals(p2));
        System.out.println("  hashCode  : " + (p.hashCode() == p2.hashCode()));

        Set<Person> set = new HashSet<>(List.of(p, p2));
        System.out.println("  HashSet   : " + set.size() + " phần tử (nhét vào 2)");

        // Record đã extends java.lang.Record ngầm -> implicit final, không kế thừa được.
        System.out.println("  cha của Person: " + Person.class.getSuperclass().getName());
    }

    private static void khongSuaDuoc() {
        System.out.println();
        System.out.println("=== 2. Immutability: 'sửa' = dựng cái mới ===");

        Person goc = new Person("Thiên", 30, "thienlv@paraline.io");
        Person moi = goc.doiEmail("thien@gmail.com");

        // Không có setEmail(). Object cũ không hề thay đổi.
        System.out.println("  gốc sau khi doiEmail: " + goc);
        System.out.println("  bản mới             : " + moi);
        System.out.println("  là hai object khác  : " + (goc != moi));

        Point a = new Point(3, 4);
        Point b = a.tinhTien(1, 1);
        System.out.println("  Point gốc : " + a);
        System.out.println("  sau tịnh tiến: " + b);

        // Hệ quả thực tế: chia sẻ qua nhiều thread không cần khoá, không cần defensive copy.
        System.out.println("  -> an toàn thread, cache được, dùng làm key của Map được");
    }

    private static void validateTaiBien() {
        System.out.println();
        System.out.println("=== 3. Validate tại constructor: không có trạng thái nửa vời ===");

        thu("name rỗng", () -> new Person("   ", 30, "a@b.com"));
        thu("age âm", () -> new Person("Thiên", -1, "a@b.com"));
        thu("age 200", () -> new Person("Thiên", 200, "a@b.com"));
        thu("email sai", () -> new Person("Thiên", 30, "a@b"));
        thu("email null", () -> new Person("Thiên", 30, null));

        // doiEmail cũng bị chặn: mọi đường vào đều đi qua compact constructor.
        Person p = new Person("Thiên", 30, "a@b.com");
        thu("doiEmail sai", () -> p.doiEmail("hỏng"));

        thu("Point NaN", () -> new Point(Double.NaN, 0));
        // tinhTien không tự validate, nhưng nó gọi new Point() nên vẫn bị chặn.
        thu("tinhTien NaN", () -> new Point(1, 1).tinhTien(Double.NaN, 0));
    }

    private static void chuanHoaDoiNghiaEquals() {
        System.out.println();
        System.out.println("=== 4. Chuẩn hoá trong constructor định nghĩa lại 'trùng nhau' ===");

        Person a = new Person("Thiên", 30, "A@B.COM");
        Person b = new Person("  Thiên  ", 30, "a@b.com");

        System.out.println("  a = " + a);
        System.out.println("  b = " + b);
        System.out.println("  equals : " + a.equals(b));
        System.out.println("  HashSet: " + new HashSet<>(List.of(a, b)).size() + " phần tử");
        System.out.println("  -> hai request gửi 'A@B.COM' và 'a@b.com' không tạo 2 user trùng");
    }

    // record lồng trong class: hợp lệ, và static ngầm.
    record DonHangHo(String ma, List<String> items) {
    }

    record DonHangAnToan(String ma, List<String> items) {
        DonHangAnToan {
            items = List.copyOf(items);
        }
    }

    private static void batBaiNong() {
        System.out.println();
        System.out.println("=== 5. CÁI BẪY: immutable chỉ NÔNG (shallow) ===");

        List<String> nguon = new ArrayList<>(List.of("bút", "vở"));

        DonHangHo ho = new DonHangHo("DH-01", nguon);
        System.out.println("  trước: " + ho);
        nguon.add("kẻ gian thêm vào");          // sửa list gốc
        ho.items().add("và thêm qua accessor"); // hoặc sửa thẳng qua accessor
        System.out.println("  sau  : " + ho);
        System.out.println("  -> field final chỉ khoá THAM CHIẾU, không khoá nội dung List");

        List<String> nguon2 = new ArrayList<>(List.of("bút", "vở"));
        DonHangAnToan antoan = new DonHangAnToan("DH-02", nguon2);
        nguon2.add("kẻ gian thêm vào");
        System.out.println("  copyOf, sửa nguồn: " + antoan);
        thu("copyOf, sửa qua accessor", () -> antoan.items().add("x"));
    }

    private static void bayDouble() {
        System.out.println();
        System.out.println("=== 6. equals của record trên double ===");

        System.out.println("   0.0 == -0.0            : " + (0.0 == -0.0));
        System.out.println("   Point(0,0).equals(-0,0): "
                + new Point(0.0, 0.0).equals(new Point(-0.0, 0.0)));
        System.out.println("   NaN == NaN             : " + (Double.NaN == Double.NaN));
        System.out.println("  -> equals sinh tự động dùng Double.compare, không dùng ==");
    }

    private static void thu(String nhan, Runnable r) {
        try {
            r.run();
            System.out.println("  " + nhan + " -> KHÔNG nổ (bất ngờ!)");
        } catch (RuntimeException e) {
            System.out.println("  " + nhan + " -> " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());
        }
    }
}
