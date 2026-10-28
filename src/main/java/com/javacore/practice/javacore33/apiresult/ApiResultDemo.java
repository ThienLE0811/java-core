package com.javacore.practice.javacore33.apiresult;

import java.util.List;

public class ApiResultDemo {

    public static void main(String[] args) {
        switchExhaustive();
        recordPattern();
        guardWhen();
        nullTrongSwitch();
        nestedPatternVaNull();
        loadingKhongTrangThai();
        switchLaExpression();
    }

    // render: trái tim của bài 16.
    // Không có nhánh default. Compiler chấp nhận vì ApiResult sealed và 3 nhánh đã phủ hết.
    // Thêm default vào đây là tự tắt bảo hiểm: nhánh thứ 4 sau này sẽ im lặng rơi vào đó.
    //
    // Để package-private (không private) để ApiResultDemoTest cùng package gọi được.
    static String render(ApiResult<User> r) {
        return switch (r) {
            // Record pattern: vừa kiểm tra kiểu, vừa tách component ra biến, một bước.
            case Success<User>(User u) -> "OK   | " + u.name();
            case Failure<User>(String error, int code) -> "LOI  | " + code + " " + error;
            // Type pattern thường: Loading không có gì để tách.
            case Loading<User> l -> "LOAD | đang tải...";
        };
    }

    private static void switchExhaustive() {
        System.out.println("=== 1. switch exhaustive, không default ===");

        List<ApiResult<User>> ketQua = List.of(
                new Success<>(new User(1, "Thiên", "thienlv@paraline.io")),
                new Failure<>("không tìm thấy user", 404),
                new Loading<>());

        for (ApiResult<User> r : ketQua) {
            System.out.println("  " + render(r));
        }

        // THỬ ĐI: thêm record Timeout<T>(long ms) implements ApiResult<T> vào ApiResult.java
        // và thêm Timeout vào permits -> compiler nổ NGAY tại dòng switch ở render:
        //   error: the switch expression does not cover all possible input values
        // Đó là toàn bộ giá trị của sealed: nó chỉ thẳng vào mọi chỗ phải sửa.
        System.out.println("  -> thêm nhánh thứ 4 vào permits là switch này nổ ngay lúc compile");
    }

    // Suy luận type argument: Success(var u) thay vì Success<User>(User u).
    // Compiler biết scrutinee là ApiResult<User> nên tự điền.
    static String renderGonHon(ApiResult<User> r) {
        return switch (r) {
            case Success(var u) -> "OK   | " + u.name();
            case Failure(var error, var code) -> "LOI  | " + code + " " + error;
            case Loading<User> l -> "LOAD | đang tải...";
        };
    }

    // Nested record pattern: tách luôn component của User, không cần gọi accessor.
    static String renderChiTiet(ApiResult<User> r) {
        return switch (r) {
            case Success<User>(User(long id, String name, String email)) ->
                    "OK   | #" + id + " " + name + " <" + email + ">";
            case Failure<User>(String error, int code) -> "LOI  | " + code + " " + error;
            case Loading<User> l -> "LOAD | đang tải...";
        };
    }

    private static void recordPattern() {
        System.out.println();
        System.out.println("=== 2. Record pattern: một bước thay vì ba ===");

        ApiResult<User> r = new Success<>(new User(7, "Thiên", "thienlv@paraline.io"));

        // Cách cũ: kiểm tra kiểu -> ép kiểu -> gọi accessor. Ba lần nhắc tên Success.
        if (r instanceof Success) {
            Success<User> s = (Success<User>) r;
            System.out.println("  cũ      : " + s.data().name());
        }

        // instanceof có pattern: gộp bước 1 và 2.
        if (r instanceof Success<User> s) {
            System.out.println("  khá hơn : " + s.data().name());
        }

        // Record pattern: gộp cả ba. Không còn biến trung gian nào.
        if (r instanceof Success<User>(User u)) {
            System.out.println("  tốt     : " + u.name());
        }

        System.out.println("  suy luận: " + renderGonHon(r));
        System.out.println("  lồng    : " + renderChiTiet(r));
    }

    // Guard: nhánh có when PHẢI đứng trước nhánh không guard cùng kiểu.
    // Và when KHÔNG tính là đã phủ Failure -> vẫn cần nhánh bắt phần còn lại.
    static String renderCoGuard(ApiResult<User> r) {
        return switch (r) {
            case Failure<User>(String error, int code) when code >= 500 ->
                    "5xx  | " + code + " " + error + " (lỗi server, thử lại sau)";
            case Failure<User>(String error, int code) when code == 404 ->
                    "404  | " + error + " (không có, đừng thử lại)";
            case Failure<User>(String error, int code) ->
                    "4xx  | " + code + " " + error;
            case Success<User>(User u) -> "OK   | " + u.name();
            case Loading<User> l -> "LOAD | đang tải...";
        };
    }

    private static void guardWhen() {
        System.out.println();
        System.out.println("=== 3. Guard when: rẽ nhánh theo GIÁ TRỊ, không chỉ theo kiểu ===");

        List<ApiResult<User>> loi = List.of(
                new Failure<>("database sập", 503),
                new Failure<>("không tìm thấy", 404),
                new Failure<>("thiếu token", 401));

        for (ApiResult<User> r : loi) {
            System.out.println("  " + renderCoGuard(r));
        }
        System.out.println("  -> bỏ nhánh 4xx cuối đi: compiler báo thiếu, vì when không phủ hết");
    }

    static String renderChiuNull(ApiResult<User> r) {
        return switch (r) {
            case null -> "NULL | không có kết quả nào";
            case Success<User>(User u) -> "OK   | " + u.name();
            case Failure<User>(String error, int code) -> "LOI  | " + code + " " + error;
            case Loading<User> l -> "LOAD | đang tải...";
        };
    }

    private static void nullTrongSwitch() {
        System.out.println();
        System.out.println("=== 4. switch trên null: NPE, không rơi vào nhánh nào ===");

        thu("render(null)        ", () -> render(null));
        System.out.println("  renderChiuNull(null) -> " + renderChiuNull(null));
        System.out.println("  -> muốn nuốt null thì phải viết case null TƯỜNG MINH (Java 21+)");
    }

    private static void nestedPatternVaNull() {
        System.out.println();
        System.out.println("=== 5. CÁI BẪY: nested pattern không khớp component null ===");

        ApiResult<User> rong = new Success<>(null);

        // Success<User>(User u): u chỉ là type pattern, null vẫn bind vào u.
        thu("Success(User u)      ", () -> render(rong));

        // Success<User>(User(long id, ...)): pattern lồng YÊU CẦU data thật sự là một User.
        // null không phải User -> nhánh này trượt -> không nhánh nào khớp.
        thu("Success(User(id,..)) ", () -> renderChiTiet(rong));

        System.out.println("  -> exhaustive ở mức KIỂU, không hứa gì về null bên trong");
    }

    private static void loadingKhongTrangThai() {
        System.out.println();
        System.out.println("=== 6. Loading<T>(): record rỗng nhưng không singleton ===");

        ApiResult<User> a = new Loading<>();
        ApiResult<User> b = new Loading<>();

        System.out.println("  a == b      : " + (a == b));
        System.out.println("  a.equals(b) : " + a.equals(b));
        System.out.println("  toString    : " + a);
        System.out.println("  -> mỗi lần new là một object mới; TS chỉ cần { kind: \"loading\" }");
    }

    private static void switchLaExpression() {
        System.out.println();
        System.out.println("=== 7. switch là EXPRESSION, không phải statement ===");

        ApiResult<User> r = new Failure<>("hết hạn token", 401);

        // Trả thẳng ra giá trị. Không có break, không fall-through.
        int httpStatus = switch (r) {
            case Success<User> s -> 200;
            case Failure<User>(String error, int code) -> code;
            case Loading<User> l -> 202;
        };
        System.out.println("  httpStatus  : " + httpStatus);

        // Gộp nhiều nhánh vào một dòng khi chúng cho cùng kết quả.
        // Bắt buộc dùng _ (unnamed pattern, Java 22+): một case label KHÔNG được khai
        // hai biến binding. Viết "case Success<User> s, Failure<User> f" là lỗi compile.
        boolean xong = switch (r) {
            case Loading<User> _ -> false;
            case Success<User> _, Failure<User> _ -> true;
        };
        System.out.println("  đã kết thúc : " + xong);
        System.out.println("  -> mỗi nhánh BUỘC cho ra giá trị; thiếu một nhánh là lỗi compile");
    }

    private static void thu(String nhan, Runnable r) {
        try {
            r.run();
            System.out.println("  " + nhan + " -> KHÔNG nổ");
        } catch (RuntimeException e) {
            System.out.println("  " + nhan + " -> " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());
        }
    }
}
