package com.javacore.practice.javacore33.streams.validator;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ValidatorDemo {

    // Dữ liệu của riêng object ValidatorDemo, để inner class bên dưới có cái mà dùng tới.
    private final int doDaiToiThieu = 2;

    // Bài 14 - cách 1a: STATIC NESTED CLASS. Không cần dữ liệu nào của ValidatorDemo
    // nên khai static: new KhongRong() gọi được mà không cần một ValidatorDemo nào tồn tại.
    static class KhongRong implements Validator<String> {
        @Override
        public boolean validate(String value) {
            return !value.isBlank();
        }
    }

    // Bài 14 - cách 1b: INNER CLASS theo đúng định nghĩa - nested class KHÔNG static.
    // Nó đọc thẳng được doDaiToiThieu của outer, cái giá phải trả là mỗi instance ôm một
    // tham chiếu ngầm tới ValidatorDemo (javap thấy constructor nhận ValidatorDemo).
    // Giữ một validator kiểu này trong cache là giữ sống luôn cả object outer.
    class DuDai implements Validator<String> {
        @Override
        public boolean validate(String value) {
            return value.strip().length() >= doDaiToiThieu;
        }
    }

    public static void main(String[] args) {
        new ValidatorDemo().baCachCaiDat();
        ghepValidator();
        functionalInterfaceCoSan();
        methodReference();
        new ValidatorDemo().thisVaBienBatDuoc();
    }

    private void baCachCaiDat() {
        System.out.println("=== Bài 14: bốn cách cài cùng một interface ===");

        Validator<String> email = new EmailValidator();
        Validator<String> rong = new KhongRong();

        // Inner class phải có một instance outer mới dựng được. Ở trong method của chính
        // ValidatorDemo thì viết tắt là new DuDai(); ngoài class phải viết đầy đủ:
        // new ValidatorDemo().new DuDai() - cú pháp "new" đứng sau dấu chấm, chỉ inner class mới có.
        Validator<String> duDai = new DuDai();

        // Cách 2: anonymous class - không có tên, compiler đặt hộ là ValidatorDemo$1.
        Validator<String> dienThoai = new Validator<String>() {
            @Override
            public boolean validate(String value) {
                if (value.length() != 10 || value.charAt(0) != '0') {
                    return false;
                }
                for (int i = 0; i < value.length(); i++) {
                    if (!Character.isDigit(value.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        };

        // Cách 3: lambda - không sinh file .class nào, dựng lúc chạy bằng invokedynamic.
        Validator<String> ten = value -> value.strip().length() >= 2;

        System.out.println("static nested -> " + rong.getClass().getName());
        System.out.println("inner class   -> " + duDai.getClass().getName());
        System.out.println("anonymous     -> " + dienThoai.getClass().getName());
        System.out.println("lambda        -> " + ten.getClass().getName());
        System.out.println("  inner đọc field outer: \"Lê\" đủ dài? " + duDai.validate("Lê"));

        System.out.println("  email    a@b.com      = " + email.validate("a@b.com"));
        System.out.println("  email    a@b          = " + email.validate("a@b"));
        System.out.println("  phone    0912345678   = " + dienThoai.validate("0912345678"));
        System.out.println("  phone    912345678    = " + dienThoai.validate("912345678"));
        System.out.println("  tên      \"Lê\"         = " + ten.validate("Lê"));
        System.out.println("  tên      \" A \"        = " + ten.validate(" A "));
    }

    private static void ghepValidator() {
        System.out.println();
        System.out.println("=== default method: and / or / negate ===");

        Validator<String> email = new EmailValidator();
        Validator<String> ngan = value -> value.length() <= 20;

        // and() chưa chạy gì cả, nó chỉ dựng ra một validator mới.
        Validator<String> emailNgan = email.and(ngan);
        System.out.println("  a@b.com                      = " + emailNgan.validate("a@b.com"));
        System.out.println("  email-rat-dai@paraline.io    = "
                + emailNgan.validate("email-rat-dai@paraline.io"));

        System.out.println("  negate: a@b -> " + email.negate().validate("a@b"));

        try {
            email.and(null);
        } catch (NullPointerException e) {
            // Nổ ngay lúc ghép, không đợi tới lúc gọi validate.
            System.out.println("  and(null) -> NPE: " + e.getMessage());
        }
    }

    private static void functionalInterfaceCoSan() {
        System.out.println();
        System.out.println("=== Bài 24: functional interface có sẵn ===");

        Validator<String> email = new EmailValidator();

        // Predicate<T> - cùng hình dáng với Validator<T>, chỉ khác tên method là test().
        // email::validate là method reference dạng bound: đã có object cụ thể ở vế trái.
        Predicate<String> laEmail = email::validate;
        Predicate<String> coDuoiIo = value -> value.endsWith(".io");
        Predicate<String> emailNoiBo = laEmail.and(coDuoiIo);

        System.out.println("  thienlv@paraline.io nội bộ? " + emailNoiBo.test("thienlv@paraline.io"));
        System.out.println("  a@b.com nội bộ?             " + emailNoiBo.test("a@b.com"));
        System.out.println("  a@b không phải email?       " + laEmail.negate().test("a@b"));

        // Function<T,R> - biến đổi, andThen nối hai bước theo đúng thứ tự đọc.
        Function<String, String> boKhoangTrang = String::strip;
        Function<String, String> veChuThuong = String::toLowerCase;
        Function<String, String> chuanHoa = boKhoangTrang.andThen(veChuThuong);
        System.out.println("  chuẩn hoá \"  ThienLV@Paraline.IO \" -> \""
                + chuanHoa.apply("  ThienLV@Paraline.IO ") + "\"");

        // Consumer<T> - nhận vào, không trả gì.
        Consumer<String> inRa = System.out::println;

        // Supplier<T> - không nhận gì, trả một giá trị. Thân hàm chỉ chạy khi gọi get(),
        // nên message lỗi tốn kém cũng không bị dựng cho những giá trị hợp lệ.
        Supplier<String> thongBaoLoi = () -> "  -> email không hợp lệ";

        List<String> danhSach = List.of("thienlv@paraline.io", "a@b", "x@y.vn");
        for (String s : danhSach) {
            inRa.accept("  kiểm tra: " + s);
            if (!laEmail.test(s)) {
                inRa.accept(thongBaoLoi.get());
            }
        }

        // Ghép tất cả lại: đây là hình dáng của bài 25-30.
        System.out.println("  lọc bằng stream: "
                + danhSach.stream().filter(laEmail).toList());
    }

    private static void methodReference() {
        System.out.println();
        System.out.println("=== Bốn dạng method reference ===");

        // 1. static: Integer.parseInt(s)
        Function<String, Integer> sangSo = Integer::parseInt;

        // 2. bound - object cụ thể nằm ở vế trái, đây là System.out.
        Consumer<Object> in = System.out::println;

        // 3. unbound - không có object nào ở vế trái! Tham số đầu tiên của lambda
        //    trở thành receiver: String::isBlank tương đương s -> s.isBlank().
        Predicate<String> rong = String::isBlank;

        // 4. constructor
        Supplier<StringBuilder> taoBuilder = StringBuilder::new;

        in.accept("  static      Integer::parseInt   \"42\"  -> " + sangSo.apply("42"));
        in.accept("  bound       System.out::println       -> chính dòng này");
        in.accept("  unbound     String::isBlank    \"  \"   -> " + rong.test("  "));
        in.accept("  constructor StringBuilder::new        -> "
                + taoBuilder.get().append("rỗng lúc đầu").length() + " ký tự");
    }

    private void thisVaBienBatDuoc() {
        System.out.println();
        System.out.println("=== this, local class, biến bắt được ===");

        // LOCAL CLASS: khai báo ngay trong thân method, chỉ nhìn thấy được ở đây.
        class ChiSo implements Validator<String> {
            @Override
            public boolean validate(String value) {
                return value.chars().allMatch(Character::isDigit);
            }
        }
        System.out.println("  local class -> " + new ChiSo().getClass().getName());

        // Anonymous có this RIÊNG của nó, nên muốn đọc field outer phải ValidatorDemo.this.
        Validator<String> anon = new Validator<String>() {
            @Override
            public boolean validate(String value) {
                // getSimpleName() của anonymous class trả về chuỗi RỖNG - nó không có tên.
                System.out.println("    anonymous: this = " + this.getClass().getName()
                        + ", outer = " + ValidatorDemo.this.doDaiToiThieu);
                return true;
            }
        };

        // Lambda KHÔNG có this riêng - this vẫn là ValidatorDemo, đúng như arrow function
        // của JS không tự bind this. Nên doDaiToiThieu đọc thẳng, không cần tiền tố.
        Validator<String> lam = value -> {
            System.out.println("    lambda   : this = " + this.getClass().getSimpleName()
                    + ", outer = " + doDaiToiThieu);
            return true;
        };

        anon.validate("x");
        lam.validate("x");

        // Biến local bị bắt phải là final hoặc "effectively final" - gán lại dòng dưới là
        // compile error. JS bắt BIẾN (thấy giá trị mới), Java bắt GIÁ TRỊ tại lúc tạo.
        int nguong = 3;
        Validator<String> theoNguong = value -> value.length() >= nguong;
        // nguong = 5;   // <- bỏ comment dòng này thì cả file không biên dịch được
        System.out.println("  bắt biến nguong=" + nguong + ": \"abcd\" -> "
                + theoNguong.validate("abcd"));
    }
}
