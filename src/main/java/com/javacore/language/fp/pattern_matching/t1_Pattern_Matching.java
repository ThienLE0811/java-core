package com.javacore.language.fp.pattern_matching;

/**
 * Using Pattern Matching
 * <p>
 * 0. Vị trí bài này
 * - Bước 15/41, section Pattern Matching của lộ trình dev.java/learn. Section này chỉ có
 * MỘT trang gốc, gộp cả 5 mục trong "In this lesson" vào cùng một URL.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/pattern-matching/">dev.java - Using Pattern Matching</a>
 * - Trang gốc ghi "Last update: December 21, 2022" nên nhiều chỗ còn gọi là preview;
 * trạng thái thật tính tới JDK 25 ghi lại ở mục 6.
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - USING PATTERN MATCHING (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. GIỚI THIỆU PATTERN MATCHING
 * - (Introducing Pattern Matching)
 * - Lấy regular expression làm điểm xuất phát dễ hiểu: khớp pattern {@code \bflame\b}
 * vào một đoạn text rồi trả về vị trí khớp.
 * - BA KHÁI NIỆM NỀN, dùng lại cho cả bài:
 * + matched target: thứ cần khớp (trong ví dụ regex là bài sonnet).
 * + pattern: thứ khớp vào (biểu thức chính quy {@code flame}).
 * + result: kết quả khớp (start index, end index).
 * - Pattern matching vẫn đang được phát triển; theo dõi ở Project Amber.
 * <p>
 * 2. PATTERN MATCHING CHO INSTANCEOF
 * - (Pattern Matching for Instanceof) Final feature từ Java SE 16.
 * - Ba khái niệm ánh xạ sang {@code o instanceof String s}:
 * + matched target: vế trái của instanceof, {@code o}, kiểu gì cũng được.
 * + pattern: vế phải, một KIỂU kèm KHAI BÁO BIẾN. Kiểu có thể là class, abstract class
 * hoặc interface. Dạng này gọi là TYPE PATTERN vì nó kiểm tra kiểu.
 * + result: biến {@code s} - gọi là PATTERN VARIABLE - chỉ được tạo khi khớp, và mang
 * đúng kiểu vừa khớp. Một pattern có thể có nhiều hơn một pattern variable.
 * - Khớp theo quan hệ kế thừa: {@code String} là {@code CharSequence} nên
 * {@code o instanceof CharSequence cs} vẫn khớp với một String.
 * - PHẠM VI CỦA PATTERN VARIABLE (flow scoping): compiler cho dùng biến ở mọi chỗ
 * mà nó chắc chắn đã được tạo.
 * + Dùng được ngay trong chính biểu thức boolean: {@code o instanceof String s && !s.isEmpty()}.
 * + Dùng được SAU khối if khi nhánh {@code if (!(o instanceof String s))} thoát method
 * bằng {@code return} hoặc ném exception.
 * + Nếu nhánh phủ định KHÔNG thoát method thì biến không tồn tại sau đó - lỗi biên dịch.
 * - Khi compiler biết chắc không bao giờ khớp thì báo lỗi, ví dụ {@code Double pi} đem
 * khớp {@code instanceof String s} (String là final).
 * - Ứng dụng rõ nhất: viết lại {@code equals()} gọn hẳn:
 * {@code return o instanceof Point point && x == point.x && y == point.y;}
 * <p>
 * 3. PATTERN MATCHING CHO SWITCH
 * - (Pattern Matching for Switch) Final feature từ JDK 21, preview ở Java SE 17, 18, 19, 20.
 * - matched target là SELECTOR EXPRESSION của switch; mỗi case là một type pattern.
 * - Thay được chuỗi if-else-if dài bằng một switch đọc dễ hơn.
 * - Lợi ích hiệu năng: if-else-if là O(n) theo số nhánh, switch là O(1).
 * - Trước tính năng này, case label chỉ chấp nhận:
 * + số: {@code byte, short, char, int} - KHÔNG có {@code long}
 * + wrapper tương ứng: {@code Byte, Short, Character, Integer}
 * + {@code String}
 * + kiểu enum.
 * - GUARDED PATTERN: case label không nhận kiểu boolean nên KHÔNG viết được
 * {@code case String s && !s.isEmpty()} (lỗi biên dịch). Phải dùng mệnh đề {@code when}:
 * {@code case String s when !s.isEmpty() -> ...}. Cả cụm gọi là guarded case label,
 * gồm một type pattern và một biểu thức boolean (guard).
 * <p>
 * 4. RECORD PATTERN
 * - (Record Pattern) Final feature từ Java SE 21, preview ở Java SE 19 và 20.
 * (Bản thân record là final feature từ Java SE 16.)
 * - Record khai báo theo COMPONENT, nhờ vậy có RECORD DECONSTRUCTION:
 * {@code o instanceof Point(int x, int y)} tạo hai pattern variable x và y.
 * - Giá trị x, y lấy bằng cách GỌI ACCESSOR của record - lưu ý nếu accessor có
 * defensive copy thì bản sao đó được tạo thật.
 * - Record pattern luôn theo mẫu CANONICAL CONSTRUCTOR: dù record có thêm constructor
 * khác, {@code o instanceof Point(int x)} vẫn không biên dịch được.
 * - Mỗi component là một type pattern, nên:
 * + Suy luận kiểu được bằng {@code var}: {@code Point(var x, var y)}.
 * + Được khớp bằng một kiểu CON của kiểu thật của component:
 * {@code record Box(Object o)} khớp được {@code case Box(String s)}.
 * + Nếu kiểu đó không thể là kiểu con thì lỗi biên dịch:
 * {@code record Box(CharSequence o)} không khớp được {@code Box(Integer i)}.
 * - KHÔNG hỗ trợ boxing/unboxing: {@code record Point(Integer x, Integer y)} không khớp
 * được {@code Point(int x, int y)}.
 * - Hỗ trợ LỒNG NHAU: {@code o instanceof Circle(Point(var x, var y), var radius)}.
 * <p>
 * 5. CÒN NHỮNG PATTERN NÀO NỮA
 * - (More Patterns) Trang gốc liệt kê ba chỗ dùng pattern matching: {@code instanceof},
 * switch (statement và expression), và vòng for-each (bản gốc viết "the extends for loop").
 * - Hai loại pattern hiện có: TYPE PATTERN và RECORD PATTERN.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. TRẠNG THÁI THẬT TÍNH TỚI JDK 25 (trang gốc dừng ở 2022)
 * - instanceof pattern: final từ 16. switch pattern và record pattern: final từ 21.
 * - Pattern cho vòng for-each: BỊ RÚT khỏi JEP record pattern, hiện CHƯA có trong Java.
 * Trang gốc viết là đã hỗ trợ - cần tự kiểm chứng lại khi dùng.
 * - Unnamed pattern và unnamed variable {@code _}: final từ Java 22,
 * ví dụ {@code case Box(_)}, {@code case Point(var x, _)}.
 * - Primitive type pattern (ví dụ {@code case int i} trên một selector không phải int):
 * vẫn là preview, chưa dùng trong code thường.
 * <p>
 * 7. NULL VÀ SWITCH
 * - switch truyền thống ném {@code NullPointerException} khi selector là null.
 * - Khi có pattern label, viết được {@code case null -> ...} hoặc
 * {@code case null, default -> ...} để nhận null tường minh.
 * - Không có {@code case null} thì switch vẫn ném NPE, kể cả khi có {@code default}.
 * - Ngược lại, {@code instanceof} luôn trả về false với null, không bao giờ ném NPE.
 * <p>
 * 8. VÉT CẠN (EXHAUSTIVENESS) VỚI SEALED TYPE
 * - switch EXPRESSION bắt buộc vét cạn mọi khả năng của selector.
 * - Nếu selector là sealed interface/class, liệt kê đủ các permitted subtype thì
 * KHÔNG cần {@code default}; thêm subtype mới sau này compiler sẽ báo lỗi ngay.
 * - Đây là lý do sealed type + record + pattern matching hay đi chung
 * (mô phỏng algebraic data type).
 * <p>
 * 9. THỨ TỰ CASE - DOMINANCE
 * - Case nào có kiểu rộng hơn mà đặt trước sẽ "che" (dominate) case kiểu hẹp hơn
 * đặt sau - compiler báo lỗi chứ không im lặng.
 * - Case có guard nên đặt TRƯỚC case cùng kiểu không guard.
 * <p>
 * 10. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code instanceof String s} ≈ type narrowing của TS, nhưng TS narrow luôn chính
 * biến cũ còn Java tạo biến mới.
 * - sealed interface + record + switch ≈ discriminated union + switch trên trường kind.
 * - Record pattern ≈ destructuring của TS, khác ở chỗ Java khớp THEO VỊ TRÍ component
 * (canonical constructor) chứ không theo tên thuộc tính.
 * - TS không kiểm tra vét cạn ở runtime; Java bắt lỗi ngay lúc biên dịch.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba thành phần của pattern matching là gì, chỉ ra chúng trong {@code o instanceof String s}?
 * - Pattern variable được tạo khi nào, sống trong phạm vi nào?
 * - Vì sao {@code if (!(o instanceof String s)) return;} lại cho dùng s ở bên dưới?
 * - Khi nào compiler báo lỗi "không bao giờ khớp"?
 * - Vì sao phải dùng {@code when} thay cho {@code &&} trong case label?
 * - Trước pattern matching, switch nhận được những kiểu case label nào? Vì sao thiếu long?
 * - Record pattern lấy giá trị component bằng cách nào, hệ quả với defensive copy?
 * - Vì sao record pattern phải theo canonical constructor?
 * - Record pattern có boxing/unboxing không? Có cho khớp kiểu con không?
 * - switch pattern xử lý null thế nào, khác instanceof ra sao?
 * - Khi nào switch expression không cần {@code default}?
 */
public class t1_Pattern_Matching {

    // Record dùng cho ví dụ mục 4. Record lồng trong class là implicit static.
    record Point(double x, double y) {}

    record Circle(Point center, double radius) {}

    record Box(Object content) {}

    void main() {
        Object o = "alpha";

        // ==================================================================
        // MỤC 2 - TYPE PATTERN VỚI INSTANCEOF
        // Biến s chỉ tồn tại khi phép khớp thành công, và đã mang sẵn kiểu String:
        // không còn dòng ép kiểu (String) o như cách viết cũ.
        // ==================================================================
        if (o instanceof String s) {
            IO.println("String dài " + s.length());
        }

        // Pattern variable dùng được ngay trong phần còn lại của biểu thức boolean,
        // vì vế phải của && chỉ được đánh giá khi vế trái đã true.
        if (o instanceof String s && !s.isEmpty()) {
            IO.println("chuỗi không rỗng: " + s);
        }

        inHoaNeuLaChuoi(o);
        inHoaNeuLaChuoi(42);

        // ==================================================================
        // MỤC 3 - SWITCH VỚI TYPE PATTERN VÀ GUARD
        // ==================================================================
        IO.println(moTa(42));
        IO.println(moTa(3.14));
        IO.println(moTa("beta"));
        IO.println(moTa(""));
        IO.println(moTa(new Object()));

        // ==================================================================
        // MỤC 4 - RECORD PATTERN LỒNG NHAU
        // Một phép khớp duy nhất vừa kiểm tra kiểu Circle, vừa bóc Point bên trong,
        // vừa tạo ba biến x, y, radius. var để compiler tự suy ra kiểu double.
        // ==================================================================
        Object hinh = new Circle(new Point(1, 2), 5);
        if (hinh instanceof Circle(Point(var x, var y), var radius)) {
            IO.println("tâm (" + x + ", " + y + ") bán kính " + radius);
        }

        // Component kiểu Object khớp được bằng kiểu con của nó.
        Object hop = new Box("trong hộp");
        switch (hop) {
            case Box(String s) -> IO.println("hộp chứa chuỗi: " + s);
            case Box(Integer i) -> IO.println("hộp chứa số: " + i);
            default -> IO.println("thứ khác");
        }
    }

    // Flow scoping: nhánh phủ định THOÁT method nên bên dưới compiler chắc chắn
    // phép khớp đã thành công, s dùng được ngoài khối if.
    void inHoaNeuLaChuoi(Object o) {
        if (!(o instanceof String s)) {
            return;
        }
        IO.println(s.toUpperCase());
    }

    // Case có guard phải đứng trước case cùng kiểu không guard, nếu không sẽ bị
    // "che" (dominance) và lỗi biên dịch.
    String moTa(Object o) {
        return switch (o) {
            case Integer i -> String.format("int %d", i);
            case Double d -> String.format("double %f", d);
            case String s when !s.isEmpty() -> String.format("chuỗi không rỗng %s", s);
            case String s -> "chuỗi rỗng, length = " + s.length();
            default -> "Object " + o;
        };
    }

    // TODO 1 (mục 1): Chạy lại ví dụ regex \bflame\b trên bài sonnet, in group/start/end
    //   rồi tự chỉ ra đâu là matched target, pattern, result.
    //
    // TODO 2 (mục 2): Viết lại một method equals() kiểu cũ (if + ép kiểu) thành bản dùng
    //   instanceof pattern. Đếm số dòng giảm được.
    //
    // TODO 3 (mục 2): Viết đoạn mà nhánh if phủ định KHÔNG return, rồi dùng pattern variable
    //   bên dưới. Dán lỗi compiler vào đây.
    //
    // TODO 4 (mục 2): Thử khớp một Double với instanceof String s. Dán lỗi compiler và
    //   giải thích vì sao compiler biết trước là không khớp.
    //
    // TODO 5 (mục 3): Thử viết case String s && !s.isEmpty() để thấy lỗi, rồi sửa sang when.
    //
    // TODO 6 (mục 3): Đảo thứ tự hai case String (có guard và không guard) để gặp lỗi dominance.
    //
    // TODO 7 (mục 4): Thêm constructor phụ cho record Point rồi thử khớp Point(int x).
    //   Ghi lại lỗi - record pattern luôn theo canonical constructor.
    //
    // TODO 8 (mục 4): Đổi record Box(Object) thành Box(CharSequence) rồi giữ case Box(Integer i)
    //   để gặp lỗi "kiểu không thể khớp".
    //
    // TODO 9 (mục 4): Thử record Point(Integer x, Integer y) với pattern Point(int x, int y)
    //   để thấy record pattern không boxing/unboxing.
    //
    // TODO 10 (mục 7): Gọi moTa(null) xem có NPE không, rồi thêm case null và thử lại.
    //
    // TODO 11 (mục 8): Khai báo sealed interface Shape permits Circle, Square; viết switch
    //   expression không có default. Thêm một subtype mới để xem compiler báo lỗi vét cạn.
}
