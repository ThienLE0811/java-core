package com.javacore.language.fp.refactoring_to_functional_style;

import java.util.List;

/**
 * Converting Iteration with Transformation
 * <p>
 * 0. Vị trí bài này
 * - Bước 16/41, bài 4/5 trong section Refactoring to Functional Style của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Biến đổi dữ liệu trong vòng lặp ánh xạ sang map(); kết hợp chọn lọc rồi biến đổi ánh xạ sang filter() rồi map().
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/refactoring-to-functional-style/iteration-with-transformation/">dev.java - Converting Iteration with Transformation</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CONVERTING ITERATION WITH TRANSFORMATION (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. BIẾN ĐỔI TRONG KHI LẶP
 * - (Transforming while Iterating)
 * - Khi cần biến đổi dữ liệu trong vòng lặp (ví dụ chuyển tên thành chữ in hoa),
 * trong functional style ta dùng hàm {@code map()}.
 * <p>
 * 2. TỪ IMPERATIVE SANG FUNCTIONAL VỚI MAP
 * - (From Imperative to Functional Style)
 * - Code imperative thường gặp:
 * {@code for (String name : names) { IO.println(name.toUpperCase()); }}
 * - Nếu chỉ đổi thô sang Stream mà nhét cả biến đổi vào forEach:
 * {@code names.stream().forEach(name -> IO.println(name.toUpperCase()));}
 * → Không tốt! Lambda trong {@code forEach()} vừa biến đổi vừa in ấn: mất tính gắn kết (not cohesive),
 * khó đọc, khó sửa, vi phạm Single Responsibility.
 * - Bước trung gian: tách imperative thành 2 bước rõ ràng (biến đổi riêng, in riêng):
 * {@code String upper = name.toUpperCase(); IO.println(upper);}
 * - Khi chuyển sang functional:
 * + Vòng {@code for} → {@code stream()}
 * + Phép biến đổi {@code toUpperCase()} → thao tác trung gian {@code map()}
 * + Việc in ra → thao tác đầu cuối {@code forEach()}
 * {@code names.stream().map(String::toUpperCase).forEach(IO::println);}
 * - Thao tác {@code map()} nhận vào một {@code Function<T, R>}: biến đổi dữ liệu từ dạng này
 * sang dạng khác theo tỷ lệ 1-1.
 * <p>
 * 3. CHỌN LỌC PHẦN TỬ TRƯỚC KHI BIẾN ĐỔI
 * - (Picking Elements to Transform)
 * - Bài toán: Chỉ biến đổi và in các tên có độ dài bằng 4.
 * - Imperative:
 * {@code for (String name : names) { if (name.length() == 4) { IO.println(name.toUpperCase()); } }}
 * - Functional: kết hợp {@code filter()} đi trước {@code map()}:
 * {@code names.stream().filter(name -> name.length() == 4).map(String::toUpperCase).forEach(IO::println);}
 * - Thứ tự quan trọng: {@code filter()} loại bỏ sớm các dữ liệu không mong muốn, {@code map()}
 * chỉ phải thực hiện biến đổi trên các phần tử đã vượt qua cánh cổng lọc → vừa rõ nghĩa vừa tối ưu hiệu năng.
 * <p>
 * 4. BẢNG ÁNH XẠ
 * - (Mappings)
 * - {@code foreach} có biến đổi dữ liệu           → {@code stream().map(...)}
 * - {@code foreach} có {@code if} rồi biến đổi    → {@code stream().filter(...).map(...)}
 * - "Anywhere you see transformation of data within a for loop, use the map() function
 * to perform the transformation in the functional style. In addition, if the body of
 * the loop has a if statement to selectively some value for transformation, then use
 * the stream() API with the call to the filter() method before using the map() method."
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. MAP VS FLATMAP VS PRIMITIVE MAP
 * - {@code map}: biến đổi 1-to-1 (1 String → 1 String hoặc 1 Object khác).
 * - {@code flatMap}: biến đổi 1-to-N hoặc làm phẳng (flatten) lồng nhau (ví dụ List các List).
 * - Tránh boxing khi đổi sang số nguyên thủy: dùng {@code mapToInt()}, {@code mapToLong()},
 * {@code mapToDouble()} thay vì {@code map(String::length)} để được {@code IntStream} chuyên dụng,
 * tiết kiệm bộ nhớ và gọi được các phép tính số học như {@code .sum()}, {@code .average()}.
 * <p>
 * 6. TÍNH GẮN KẾT (COHESION) VÀ KHÔNG GÂY TÁC DỤNG PHỤ (PURE FUNCTION)
 * - Lambda truyền vào {@code map()} NÊN là hàm thuần túy (pure function): không làm biến đổi
 * trạng thái bên ngoài (không gán biến, không ghi file, không in log).
 * - Mọi side-effect nên dành riêng cho terminal operation như {@code forEach()} ở cuối pipeline.
 * <p>
 * 7. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Trong JS: {@code names.filter(...).map(...)} chạy qua 2 vòng lặp riêng biệt, cấp phát 2 mảng mới liên tiếp!
 * - Trong Java Stream: cơ chế "Loop Fusion" (kết hợp vòng lặp) xử lý từng phần tử một xuyên suốt toàn bộ
 * pipeline (chạy filter rồi map ngay cho phần tử đó) trong một lượt duy nhất, không tạo cấu trúc dữ liệu trung gian.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao không nên nhét cả việc biến đổi lẫn in ấn vào trong cùng một lambda của forEach()?
 * - map() nhận functional interface nào? Kiểu đầu vào và kiểu đầu ra có nhất thiết phải giống nhau không?
 * - Vì sao nên đặt filter() trước map() thay vì ngược lại?
 * - mapToInt() khác map() ở điểm nào, khi nào bắt buộc nên dùng mapToInt()?
 * - Loop Fusion trong Java Stream hoạt động thế nào khác với filter().map() trong JavaScript?
 */
public class t4_IterationWithTransformation {

    void main() {
        List<String> names = List.of("Jack", "Paula", "Kate", "Peter");

        // ==================================================================
        // MỤC 2 - BIẾN ĐỔI DỮ LIỆU KHI LẶP: DÙNG MAP
        // ==================================================================

        // Imperative thô: biến đổi và in chung một dòng.
        for (String name : names) {
            IO.println(name.toUpperCase());
        }

        // Functional chưa tốt (thiếu cohesion): nhét toUpperCase vào forEach.
        names.stream()
                .forEach(name -> IO.println(name.toUpperCase()));

        // Functional chuẩn: tách bạch biến đổi (map) và tiêu thụ kết quả (forEach).
        names.stream()
                .map(String::toUpperCase)
                .forEach(IO::println);

        // ==================================================================
        // MỤC 3 - CHỌN LỌC RỒI BIẾN ĐỔI: FILTER ĐI TRƯỚC MAP
        // ==================================================================

        // Imperative.
        for (String name : names) {
            if (name.length() == 4) {
                IO.println(name.toUpperCase());
            }
        }

        // Functional: filter lọc trước để map chỉ xử lý các phần tử hợp lệ.
        names.stream()
                .filter(name -> name.length() == 4)
                .map(String::toUpperCase)
                .forEach(IO::println);
    }

    // TODO 1 (mục 2): Viết danh sách tên người, dùng map để đổi thành độ dài của từng tên
    //   (String -> Integer), sau đó in ra.
    //
    // TODO 2 (mục 5): Đổi ví dụ ở TODO 1 sang dùng mapToInt thay vì map. Dùng phương thức
    //   .sum() để tính tổng số ký tự của tất cả các tên.
    //
    // TODO 3 (mục 3): Thử hoán đổi thứ tự giữa filter và map:
    //   names.stream().map(String::toUpperCase).filter(...)
    //   So sánh số lần toUpperCase() phải chạy khi filter đặt trước vs khi filter đặt sau.
    //
    // TODO 4 (mục 6): Viết một pipeline map cố tình sửa một biến counter bên ngoài:
    //   Giải thích vì sao compiler cấm hoặc vì sao đây là bad practice trong lập trình hàm.
}
