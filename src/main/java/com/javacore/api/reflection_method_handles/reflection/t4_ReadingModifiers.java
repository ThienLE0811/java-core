package com.javacore.api.reflection_method_handles.reflection;

/**
 * Reading Modifiers
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 4/13 của section The Reflection API.
 * - Bài ngắn, một khái niệm: {@code getModifiers()} trả về một {@code int} mà mỗi BIT là một
 * modifier, và lớp {@code Modifier} là bộ giải mã.
 * - Một câu cho cả bài: modifier được gói thành bitmask từ thời Java 1.1, nên phải giải mã -
 * Java 20 mới cho một API tử tế là {@code accessFlags()}.
 * - Bài này khép lại nhóm "đọc thông tin". Từ bài t5 trở đi là TÁC ĐỘNG, và lúc đó
 * {@code Modifier} trở thành công cụ kiểm tra bắt buộc: biết field có {@code static} không
 * mới biết truyền {@code null} hay truyền object vào {@code get()}.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/modifiers/">dev.java - Reading Modifiers</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Output trong note đã chạy
 * thật trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - READING MODIFIERS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. MODIFIER CÓ Ở ĐÂU
 * - "Many elements have modifiers in the Java language: classes, fields, methods,
 * constructors, or method and constructor parameters."
 * - Chúng làm ba việc: đổi tầm nhìn, quy định có ghi đè được không, quy định có sửa được không.
 * - "The Reflection API gives access to these modifiers, once you have a reference on the
 * element you need to query." Chữ ONCE nhắc lại: vẫn phải có {@code Class} hoặc
 * {@code Member} trước đã (bài t2).
 * <p>
 * 2. DANH SÁCH MODIFIER THEO TRANG GỐC
 * - Trang gốc phân nhóm theo CHỨC NĂNG chứ không liệt kê suông, đáng chép lại:
 * + Access modifiers: {@code public}, {@code protected}, {@code private}
 * + Bắt buộc ghi đè: {@code abstract}
 * + Hạn chế ghi đè: {@code sealed}, {@code non-sealed}
 * + Thành viên thuộc về lớp bao chứ không thuộc instance: {@code static}
 * + Cấm sửa giá trị hoặc cấm mở rộng: {@code final}
 * + Ép hành vi dấu phẩy động chặt: {@code strictfp}
 * + Annotations
 * - "Not all modifiers are allowed on all classes or all members, for example an interface
 * cannot be final and an enum cannot be abstract."
 * - CẢNH BÁO quan trọng nhất của bài, nguyên văn: "The class java.lang.reflect.Modifier
 * contains declarations for SOME possible modifiers (IT DOES NOT CONTAIN sealed AND
 * non-sealed)."
 * + Tức là {@code Modifier} đã lỗi thời so với ngôn ngữ. Muốn biết lớp có {@code sealed}
 * không thì phải dùng {@code Class.isSealed()} (Java 17), không có
 * {@code Modifier.isSealed()}.
 * <p>
 * 3. GETMODIFIERS() VÀ LỚP MODIFIER
 * - (Using getModifiers())
 * - "This method returns an int, in which each bit represents a modifier. For instance, if
 * the bit 0 is set, it means that the corresponding member is public. If the bit 3 is set,
 * then the corresponding member is static."
 * - "Having to decode the modifiers int would be cumbersome. Fortunately, the class Modifier
 * is there to help you. This class provides a collection of static methods, one for each
 * modifier, that decodes this integer for you."
 * - Hai ví dụ của trang gốc:
 * <pre>
 *   String:      isFinal = true    isAbstract = false
 *   Collection:  isFinal = false   isAbstract = true
 * </pre>
 * - Giải thích cho ca thứ hai, nguyên văn: "Since Collection is an interface, it is
 * implicitly abstract. The compiler adds this modifier for every interface."
 * + Bài học rộng hơn: {@code getModifiers()} trả về modifier TRONG BYTECODE, không phải
 * modifier bạn GÕ trong mã nguồn. Trình biên dịch thêm vào những thứ ngầm định - interface
 * thành {@code abstract}, member của interface thành {@code public}, enum thành
 * {@code final}. Đừng ngạc nhiên khi thấy modifier mình không viết.
 * <p>
 * 4. MEMBER INTERFACE VÀ ACCESSFLAGS()
 * <p>
 * - 4a. HAI CHỖ KHAI BÁO GETMODIFIERS()
 * + "There are two declarations of this getModifiers() method: one on the Class class, and
 * another one on the Member interface, that is implemented by the following classes: Field,
 * Method, and Constructor."
 * + "Both methods work in the same way: they return the same int that you need to decode
 * using the factory methods of the Modifier class." Học một lần dùng cho cả bốn chỗ.
 * <p>
 * - 4b. ACCESSFLAGS() - API MỚI CỦA JAVA 20
 * + "Both the Class class and the Member interface saw the addition of an accessFlags()
 * method in Java SE 20. This method returns a set of AccessFlag instances, that makes the
 * reading of modifiers easier."
 * + {@code AccessFlag} là một enum, nên thay vì giải mã bit thì bạn có một
 * {@code Set} duyệt được, in được, {@code contains} được.
 * + Kết quả ví dụ trang gốc cho {@code String}: {@code PUBLIC}, {@code FINAL},
 * {@code SUPER}. Đã chạy lại trên JDK 25, đúng y như vậy.
 * + {@code SUPER} là cờ bytecode cũ không tương ứng với từ khóa nào trong ngôn ngữ - nó có
 * trên gần như mọi lớp. Đây là minh họa cho câu kết của trang gốc: "Some modifiers and
 * access flags have a one-to-one correspondence, but not all of them."
 * + Chạy thêm trên {@code Collection} ra {@code PUBLIC}, {@code INTERFACE},
 * {@code ABSTRACT} - khớp với mục 3.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. VÌ SAO LẠI LÀ BITMASK - VÀ VÌ SAO ĐIỀU ĐÓ QUAN TRỌNG VỚI BẠN
 * - Đây là thiết kế từ Java 1.1, ánh xạ thẳng cấu trúc {@code access_flags} trong file
 * {@code .class}. Một {@code int} 32 bit, mỗi modifier một bit - gọn cho JVM, khó cho người.
 * - Hệ quả thực tế: {@code getModifiers()} trả {@code int}, nên nó KHÔNG an toàn kiểu. So
 * sánh {@code modifiers == Modifier.PUBLIC} là SAI trong đa số trường hợp, vì một member có
 * thể vừa {@code public} vừa {@code static} vừa {@code final}. Luôn dùng
 * {@code Modifier.isPublic(mods)}, hoặc phép AND bit.
 * - Hệ quả thứ hai: không mở rộng được. Hết bit là hết chỗ - đó là một phần lý do
 * {@code sealed} không có trong {@code Modifier} và vì sao Java 20 phải làm
 * {@code accessFlags()}.
 * <p>
 * 6. DÙNG MODIFIER HAY ACCESSFLAGS - KHUYẾN NGHỊ
 * - Code mới trên JDK 20 trở lên: {@code accessFlags()}. Nó trả {@code Set}, đọc được,
 * in ra log được nguyên vẹn, và dùng {@code contains(AccessFlag.STATIC)} thì không ai đọc
 * nhầm.
 * - Vẫn phải biết {@code Modifier} vì: mọi code và mọi thư viện hiện có đều dùng nó, và
 * nhiều dự án còn trên JDK 17 hoặc 11.
 * - Cả hai đều KHÔNG cho biết {@code sealed}. Dùng {@code Class.isSealed()} và
 * {@code Class.getPermittedSubclasses()}.
 * - Một số câu hỏi có method riêng gọn hơn hẳn, nên nhớ để khỏi đi đường vòng qua bitmask:
 * {@code isInterface()}, {@code isEnum()}, {@code isRecord()}, {@code isArray()},
 * {@code isPrimitive()}, {@code isAnonymousClass()}, {@code isSynthetic()}.
 * <p>
 * 7. BA MODIFIER BẠN SẼ THỰC SỰ PHẢI KIỂM TRA KHI VIẾT CODE REFLECTION
 * - {@code static}: quyết định tham số đầu tiên của {@code Field.get} và
 * {@code Method.invoke} là {@code null} hay là object. Quên kiểm tra là
 * {@code NullPointerException} hoặc {@code IllegalArgumentException}. Đây là lỗi số một của
 * người mới viết code quét field.
 * - {@code synthetic}: trình biên dịch sinh ra các member bạn không hề viết - field
 * {@code this$0} của inner class, bridge method khi ghi đè có đổi kiểu trả về, field
 * {@code $jacocoData} khi đo coverage. Vòng lặp quét field mà không lọc
 * {@code isSynthetic()} sẽ gặp chúng và hỏng theo cách rất khó hiểu. Jackson và Spring đều
 * lọc.
 * - {@code transient}: không phải chuyện của JVM mà là quy ước của tầng serialize. Nếu bạn
 * tự viết bộ chuyển đổi object sang JSON hay sang bản ghi DB thì phải tự tôn trọng nó.
 * - Bộ lọc gần như mọi framework dùng khi quét field thực thể: bỏ qua {@code static}, bỏ qua
 * {@code synthetic}, bỏ qua {@code transient}.
 * <p>
 * 8. FINAL KHÔNG CÒN LÀ RÀO CẢN - VÀ CŨNG KHÔNG CÒN LÀ CỬA SAU
 * - Nối tiếp bước 26 mục 4a: {@code setAccessible(true)} rồi {@code set()} lên field
 * {@code final} của lớp thường vẫn ghi được.
 * - Nhưng ba ngoại lệ phải nhớ: field của {@code record} thì không
 * ({@code IllegalAccessException}); {@code static final} thì không; và
 * {@code static final} kiểu nguyên thủy hoặc {@code String} đã bị trình biên dịch nội tuyến
 * nên có sửa được giá trị thì code đọc nó vẫn thấy giá trị cũ.
 * - Nói cách khác: {@code Modifier.isFinal()} cho bạn biết sự thật về KHAI BÁO, không cho
 * biết bạn có ghi được hay không. Hai câu hỏi khác nhau.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - TS có {@code public}, {@code private}, {@code protected}, {@code readonly} nhưng tất cả
 * đều bị XÓA khi biên dịch sang JS - không còn gì để đọc lúc chạy.
 * - JS thật sự có một thứ gần giống: {@code Object.getOwnPropertyDescriptor(obj, key)} trả
 * về {@code writable}, {@code enumerable}, {@code configurable}. Đây là bộ "modifier" của
 * JS, và nó cũng là thứ đọc được lúc chạy như {@code getModifiers()}.
 * - Khác biệt lớn: {@code readonly} của TS là ảo (chỉ compiler biết), còn {@code final} của
 * Java là thật trong bytecode và JVM có cưỡng chế - chỉ reflection mới lách được.
 * - {@code #private} của JS mới là tương đương gần nhất với {@code private} của Java, và
 * ngược đời là nó CHẶT HƠN: không có {@code setAccessible} nào cho nó.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code getModifiers()} trả về kiểu gì, và vì sao kiểu đó?
 * - Vì sao {@code modifiers == Modifier.PUBLIC} gần như luôn sai?
 * - {@code Modifier} thiếu hai modifier nào của ngôn ngữ hiện đại? Thay bằng gì?
 * - Vì sao {@code Collection} là {@code abstract} dù không ai gõ từ đó?
 * - {@code accessFlags()} có từ Java mấy, trả về gì, hơn {@code Modifier} ở điểm nào?
 * - {@code SUPER} trong kết quả {@code accessFlags()} của {@code String} là gì?
 * - Hai nơi nào khai báo {@code getModifiers()}? Lớp nào cài {@code Member}?
 * - Ba modifier phải lọc khi quét field của một entity, và vì sao từng cái?
 * - {@code Modifier.isFinal()} trả {@code true} thì có chắc không ghi được field đó không?
 */
public class t4_ReadingModifiers {

    void main() {
        // TODO 1 (mục 3): In isFinal và isAbstract cho String, Collection, ArrayList,
        //   một abstract class tự viết, và một enum. Lập bảng và giải thích mỗi dòng.
        //
        // TODO 2 (mục 3): In Integer.toBinaryString(c.getModifiers()) cho String.class và
        //   cho một field private static final. Đối chiếu từng bit với hằng số
        //   Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL.
        //
        // TODO 3 (mục 3): Dùng Modifier.toString(modifiers) để in cả chuỗi modifier một lần.
        //   So sánh tiện dụng với việc gọi từng isXxx().
        //
        // TODO 4 (mục 2, mục 6): Tạo một sealed interface với hai lớp con. Thử tìm
        //   Modifier.isSealed() - xác nhận KHÔNG có. Rồi dùng Class.isSealed() và
        //   getPermittedSubclasses().
        //
        // TODO 5 (mục 4b): In accessFlags() của String, Collection, một record, một enum,
        //   và một method private static. Dán output và so sánh với kết quả Modifier.
        //
        // TODO 6 (mục 5): Viết "if (modifiers == Modifier.PUBLIC)" cho một field vừa public
        //   vừa static. Xác nhận nó trả false. Sửa lại cho đúng bằng hai cách: Modifier.
        //   isPublic và phép AND bit.
        //
        // TODO 7 (mục 7): Tạo một lớp có inner class non-static. In getDeclaredFields() của
        //   inner class đó và tìm field this$0. Kiểm tra isSynthetic() của nó.
        //
        // TODO 8 (mục 7): Viết hàm fieldsToPersist(Class<?> c) trả về danh sách field sau khi
        //   lọc bỏ static, synthetic, transient. Test trên một lớp có đủ cả ba loại.
        //
        // TODO 9 (mục 7): Viết lớp cha có method "Object get()" và lớp con ghi đè thành
        //   "String get()". In getDeclaredMethods() của lớp con - tìm bridge method và xác
        //   nhận isBridge() cùng isSynthetic() đều true.
        //
        // TODO 10 (mục 8): Thử setAccessible + set lên bốn loại field: final thường,
        //   static final int, static final String, và field của record.
        //   Lập bảng bốn kết quả: ghi được, ném exception, hay ghi được mà không có tác dụng.
    }
}
