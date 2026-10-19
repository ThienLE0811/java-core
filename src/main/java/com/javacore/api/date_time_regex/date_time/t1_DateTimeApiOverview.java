package com.javacore.api.date_time_regex.date_time;

import java.time.LocalDate;

/**
 * The Date Time API Overview
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 1/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Bài mở màn: dựng từ vựng chung (ISO-8601, CLDR, TZDB, năm package, bộ tiền tố method).
 * Chưa đi vào lớp cụ thể, các bài sau mổ xẻ từng lớp.
 * - Một câu cho cả bài: {@code java.time} được thiết kế quanh BỐN nguyên tắc và MỘT bộ
 * tiền tố method nhất quán - thuộc bộ tiền tố là đoán được tên method của lớp chưa từng gặp.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/intro/">dev.java - The Date Time API Overview</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - THE DATE TIME API OVERVIEW (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. VÌ SAO THỜI GIAN LÀ BÀI TOÁN KHÓ
 * - (Introducing Date Time APIs)
 * - Ví dụ trang gốc đưa ra: "The result of adding one month to January 31 is different for
 * a leap year than for other years." Cộng một tháng vào 31/01 ra 28/02 hay 29/02 là tùy năm.
 * - Múi giờ làm mọi thứ rối thêm: luật daylight saving thay đổi theo quốc gia và theo thời điểm,
 * không đoán trước được.
 * - Java dùng "the calendar system defined in ISO-8601 as the default calendar". Lớp lõi:
 * {@code LocalDateTime}, {@code ZonedDateTime}, {@code OffsetDateTime}.
 * - Lịch khác (Hijrah, Thai Buddhist, Japanese, Minguo) nằm ở package {@code java.time.chrono},
 * học ở bài t12.
 * - API lấy dữ liệu từ HAI nguồn chuẩn bên ngoài:
 * + "Unicode Common Locale Data Repository (CLDR)" - tên tháng, tên thứ, định dạng theo
 * hàng trăm ngôn ngữ.
 * + "the Time-Zone Database (TZDB)" - luật múi giờ, có dữ liệu lịch sử từ 1970.
 * <p>
 * 2. BỐN NGUYÊN TẮC THIẾT KẾ
 * - (Date Time Design Principles)
 * - CLEAR: method có hành vi rõ ràng, không mập mờ. "Invoking a Date-Time method with a null
 * parameter value typically triggers a {@code NullPointerException}." Tức là API KHÔNG im lặng
 * nuốt null - đây là lựa chọn có chủ đích, không phải thiếu sót.
 * - FLUENT: method trả về đối tượng nên nối chuỗi được, "a fluent interface, making the code
 * easy to read".
 * - IMMUTABLE: "Most of the classes in the Date-Time API create objects that are immutable."
 * Hệ quả: thread-safe, và KHÔNG có setter. Thay setter là các factory method tiền tố
 * {@code of}, {@code from}, {@code with}.
 * - EXTENSIBLE: tự viết được adjuster, query, thậm chí cả hệ lịch riêng - "define your own
 * time adjusters and queries, or build your own calendar system".
 * <p>
 * 3. NĂM PACKAGE
 * - (The Date Time Packages)
 * - {@code java.time} - lớp lõi theo ISO-8601: date, time, time zone, instant, duration, clock.
 * Đây là package dùng 95% thời gian.
 * - {@code java.time.chrono} - hệ lịch không phải ISO (bài t12).
 * - {@code java.time.format} - parse và format (bài t8).
 * - {@code java.time.temporal} - API mở rộng cho người viết framework, định nghĩa field và
 * unit (bài t9).
 * - {@code java.time.zone} - lớp hỗ trợ múi giờ, trong đó có {@code ZoneRules} (bài t6).
 * <p>
 * 4. BẢNG TIỀN TỐ METHOD - PHẦN ĐÁNG THUỘC NHẤT CỦA BÀI
 * - (Method Naming Conventions)
 * - API cố tình đặt tên nhất quán giữa MỌI lớp. Thuộc bảng này là đọc được cả API.
 * <p>
 * .  Tiền tố   Loại            Ý nghĩa
 * .  of        static factory  tạo instance, có kiểm tra tham số đầu vào
 * .  from      static factory  chuyển đổi từ kiểu khác sang kiểu đích
 * .  parse     static factory  parse chuỗi thành instance
 * .  format    instance        format đối tượng thành chuỗi
 * .  get       instance        lấy một phần trạng thái
 * .  is        instance        hỏi một tính chất của trạng thái
 * .  with      instance        trả về BẢN SAO đã đổi - tương đương setter của thế giới bất biến
 * .  plus      instance        trả về bản sao đã cộng thêm
 * .  minus     instance        trả về bản sao đã trừ bớt
 * .  to        instance        chuyển sang kiểu khác
 * .  at        instance        ghép với một đối tượng khác
 * <p>
 * - Mẹo nhớ hai cặp dễ lẫn:
 * + {@code of} nhận các THÀNH PHẦN rời ({@code LocalDate.of(2026, 10, 19)});
 * {@code from} nhận một ĐỐI TƯỢNG temporal khác ({@code LocalDate.from(zonedDateTime)}).
 * + {@code at} GHÉP thêm thông tin ({@code localDate.atTime(9, 0)} ra {@code LocalDateTime});
 * {@code to} CHUYỂN kiểu ({@code localDate.toEpochDay()}).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. API NÀY TỪ ĐÂU RA
 * - {@code java.time} vào Java SE 8 (2014) qua JSR-310, do Stephen Colebourne dẫn dắt - cũng
 * chính là tác giả thư viện Joda-Time. Nói cách khác đây là bản làm lại có rút kinh nghiệm
 * của một thư viện đã chạy production nhiều năm.
 * - Vì vậy đừng dùng Joda-Time cho dự án mới: chính tác giả của nó khuyên chuyển sang
 * {@code java.time}.
 * - Trước đó Java chỉ có {@code java.util.Date} (1996) và {@code java.util.Calendar} (1997).
 * Bài t13 nói vì sao hai lớp đó hỏng.
 * <p>
 * 6. BẤT BIẾN - BẪY SỐ MỘT CỦA NGƯỜI TỪ JAVASCRIPT SANG
 * - Mọi method {@code plus/minus/with} trả về ĐỐI TƯỢNG MỚI, không sửa đối tượng gốc.
 * - Viết {@code date.plusDays(1);} rồi dùng tiếp {@code date} là KHÔNG CÓ TÁC DỤNG GÌ.
 * Phải {@code date = date.plusDays(1);}.
 * - Đây là lỗi im lặng: compile được, chạy được, chỉ sai kết quả. IDE thường cảnh báo
 * "Result of ... is ignored" - đừng tắt cảnh báo đó.
 * - Đổi lại: đối tượng {@code java.time} dùng làm key của {@code Map}, dùng chung giữa nhiều
 * thread, làm hằng {@code static final} đều an toàn, không cần sao chép phòng vệ.
 * <p>
 * 7. DỮ LIỆU TZDB SỐNG CÙNG JDK - CHUYỆN THẬT Ở PRODUCTION
 * - Luật múi giờ là DỮ LIỆU, không phải code. Các nước đổi luật DST khá thường xuyên,
 * IANA phát hành bản TZDB mới vài lần mỗi năm.
 * - JDK đóng gói sẵn một bản TZDB. JDK cũ không vá = múi giờ sai sau khi một nước đổi luật,
 * dù code hoàn toàn đúng.
 * - Hệ quả vận hành: container chạy image JDK cũ có thể tính sai giờ của khách hàng nước ngoài.
 * Cách xử lý: cập nhật JDK, hoặc dùng {@code tzupdater} của Oracle cho bản không nâng cấp được.
 * - Việt Nam không có DST nên dev Việt rất dễ quên vấn đề này cho tới khi có khách nước ngoài.
 * <p>
 * 8. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Date} của JS gần giống {@code java.util.Date} cũ: MUTABLE ({@code d.setDate(...)}
 * sửa tại chỗ), tháng đánh số từ 0, luôn gắn với múi giờ hệ thống. Toàn bộ nhược điểm mà
 * {@code java.time} sinh ra để vá.
 * - Hệ sinh thái JS phải bù bằng thư viện ngoài: moment (đã ngừng phát triển), date-fns,
 * dayjs, luxon. Java KHÔNG cần thư viện ngoài cho việc này - đừng tìm "thư viện date tốt nhất
 * cho Java", câu trả lời là {@code java.time} có sẵn trong JDK.
 * - TC39 đang chuẩn hóa {@code Temporal} cho JS, và nó mượn thẳng thiết kế của
 * {@code java.time}: {@code Temporal.PlainDate} ≈ {@code LocalDate},
 * {@code Temporal.PlainDateTime} ≈ {@code LocalDateTime},
 * {@code Temporal.Instant} ≈ {@code Instant},
 * {@code Temporal.ZonedDateTime} ≈ {@code ZonedDateTime}. Học section này xong là biết luôn
 * {@code Temporal} của JS.
 * - Chữ "Plain" của JS tương đương chữ "Local" của Java: KHÔNG mang múi giờ. Nhớ cặp từ này
 * để khỏi lẫn ở bài t5 và t6.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Lịch mặc định của {@code java.time} là lịch nào? Muốn lịch khác thì vào package nào?
 * - CLDR và TZDB mỗi cái cung cấp dữ liệu gì?
 * - Kể bốn nguyên tắc thiết kế, mỗi cái một câu.
 * - Truyền {@code null} vào method của {@code java.time} thì chuyện gì xảy ra?
 * - Năm package, mỗi package một câu về công dụng.
 * - Phân biệt {@code of} với {@code from}; phân biệt {@code at} với {@code to}.
 * - Vì sao API bất biến lại không có setter, và cái gì thay thế setter?
 * - {@code date.plusDays(1);} đứng một mình làm gì? Vì sao đây là lỗi nguy hiểm?
 * - Vì sao chạy JDK cũ có thể tính sai múi giờ dù code đúng?
 */
public class t1_DateTimeApiOverview {

    void main() {
        // TODO 1 (mục 2, 6): Tạo một LocalDate, gọi date.plusDays(10); mà KHÔNG gán lại,
        //   rồi in date. Sau đó gán lại và in. Dán hai output cạnh nhau - đây là bài học
        //   quan trọng nhất của cả section, làm một lần cho nhớ đời.
        //
        LocalDate date = LocalDate.of(2026, 10, 19);

        date.plusDays(10);                 // kết quả bị VỨT ĐI
        System.out.println("sau khi goi plusDays(10) : " + date);

        date = date.plusDays(10);          // phải GÁN LẠI
        System.out.println("sau khi gan lai         : " + date);
        // TODO 2 (mục 2): Gọi LocalDate.of(2026, 10, null-able?) không được, nhưng thử
        //   LocalDate.now(null) hoặc localDate.plus(null). Dán lại exception và xác nhận
        //   đúng là NullPointerException như trang gốc nói.
        //
        // TODO 3 (mục 1): Viết LocalDate.of(2024, 1, 31).plusMonths(1) và
        //   LocalDate.of(2023, 1, 31).plusMonths(1). In cả hai. Đây chính là ví dụ mở đầu
        //   của trang gốc - xác nhận bằng code thật.
        //
        // TODO 4 (mục 4): Với LocalDate hôm nay, gọi lần lượt mỗi tiền tố một method:
        //   of, from, parse, format, get, is, with, plus, minus, to, at. Mười một dòng,
        //   mỗi dòng in kết quả. Đây là bài tập thuộc bảng tiền tố.
        //
        // TODO 5 (mục 4): Dùng IDE autocomplete gõ "LocalDate." rồi đọc lướt danh sách method.
        //   Đếm xem có bao nhiêu method bắt đầu bằng with, bao nhiêu bằng plus. Nhận xét.
        //
        // TODO 6 (mục 2): Chứng minh tính fluent: viết MỘT biểu thức nối chuỗi ít nhất bốn
        //   method để ra "ngày thứ Hai đầu tiên của tháng sau, lúc 9 giờ sáng".
        //   (Gợi ý: at..., with..., plus... - chưa cần TemporalAdjusters, bài t9 mới học.)
        //
        // TODO 7 (mục 7): In System.getProperty("java.version") và ZoneId.systemDefault().
        //   Rồi in ZoneId.getAvailableZoneIds().size() - đó là số múi giờ trong bản TZDB
        //   mà JDK này đang mang.
        //
        // TODO 8 (mục 6): Thử dùng một LocalDate làm key của HashMap, thêm vài entry.
        //   Giải thích vì sao không cần lo đối tượng key bị sửa sau khi đã put.
    }
}
