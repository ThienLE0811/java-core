package com.javacore.api.date_time_regex.date_time;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Date and Time
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 5/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Tiếp bài t4: thêm phần GIỜ. Hai lớp {@code LocalTime} và {@code LocalDateTime}.
 * - Một câu cho cả bài: chữ "Local" KHÔNG có nghĩa là "giờ máy mình" - nó có nghĩa là
 * "KHÔNG MANG múi giờ", tức là một mô tả giờ trên mặt đồng hồ chứ chưa phải một điểm
 * trên dòng thời gian.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/local-time/">dev.java - Date and Time</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - DATE AND TIME (mục 1 đến 2)
 * ======================================================================
 * <p>
 * 1. LỚP LOCALTIME
 * - (The LocalTime Class)
 * - Chứa giờ, KHÔNG có ngày.
 * - Công dụng theo trang gốc: "useful for representing human-based time of day, such as
 * movie times, or the opening and closing times of the local library". Tức là những giờ
 * LẶP LẠI mỗi ngày.
 * - Dùng được để dựng đồng hồ số.
 * - Đặc điểm phải nhớ: "The {@code LocalTime} class does not store time zone or daylight
 * saving time information."
 * <p>
 * 2. LỚP LOCALDATETIME
 * - (The LocalDateTime Class)
 * - Ghép ngày và giờ, vẫn KHÔNG có múi giờ. Trang gốc mô tả là "date (month-day-year)
 * together with time (hour-minute-second-nanosecond)", và nói rõ nó là sự kết hợp của
 * {@code LocalDate} với {@code LocalTime}.
 * - Ví dụ của trang gốc: cuộc đua đầu tiên của Louis Vuitton Cup Finals, bắt đầu lúc
 * 1:10 p.m. ngày 17/08/2013 - một sự kiện có ngày và giờ cụ thể.
 * - Cần múi giờ thì phải chuyển sang {@code ZonedDateTime} hoặc {@code OffsetDateTime}
 * (bài t6).
 * - Các method sẵn có: {@code now()}, nhóm {@code of()}, {@code from()}, và các method
 * cộng/trừ giờ, phút, ngày, tuần, tháng.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 3. "LOCAL" NGHĨA LÀ GÌ - HIỂU SAI CHỖ NÀY LÀ HỎNG CẢ SECTION
 * - "Local" KHÔNG có nghĩa "giờ địa phương của máy đang chạy".
 * - Nó có nghĩa: đối tượng này mô tả giờ NHƯ ĐỌC TRÊN MẶT ĐỒNG HỒ, và cố tình KHÔNG mang
 * thông tin đồng hồ đó đặt ở đâu.
 * - Hệ quả: {@code LocalDateTime} KHÔNG xác định được một điểm duy nhất trên dòng thời gian.
 * "14:00 ngày 19/10/2026" ở Hà Nội và ở Berlin là hai thời điểm khác nhau cách nhau 5 tiếng.
 * - Vì vậy {@code LocalDateTime} KHÔNG so sánh được với {@code Instant} nếu chưa gắn
 * {@code ZoneId}. Compiler chặn luôn - và đó là tính năng, không phải phiền phức.
 * - Chuẩn TC39 của JS gọi khái niệm này là "Plain" ({@code Temporal.PlainDateTime}), tên
 * rõ nghĩa hơn hẳn. Khi đọc "Local" cứ dịch trong đầu thành "Plain" / "trần" là đúng ý.
 * <p>
 * 4. KHI NÀO DÙNG LOCALDATETIME, KHI NÀO KHÔNG
 * - NÊN: lịch hẹn tương lai theo giờ địa phương, giờ sự kiện trong một thành phố cố định,
 * giờ nhập từ form người dùng trước khi biết múi giờ.
 * - KHÔNG NÊN: {@code created_at}, {@code updated_at}, timestamp của log, thời điểm giao dịch.
 * Những thứ này là MỐC ĐÃ XẢY RA, phải dùng {@code Instant}.
 * - Dấu hiệu dùng sai: trong code có {@code LocalDateTime.now()} rồi đem so sánh với dữ liệu
 * sinh ra từ máy khác hoặc múi giờ khác. Hai đồng hồ khác múi giờ, so trực tiếp là sai.
 * - Nhiều dự án Việt Nam dùng {@code LocalDateTime} cho tất cả và vẫn "chạy đúng", vì chỉ
 * có một múi giờ. Nó hỏng ngay ngày đầu tiên có khách nước ngoài, hoặc ngày server chuyển
 * sang chạy UTC trong container.
 * <p>
 * 5. GHÉP VÀ TÁCH - BỘ METHOD AT... VÀ TO...
 * - {@code localDate.atTime(9, 0)} → {@code LocalDateTime}; {@code localDate.atStartOfDay()}
 * → {@code LocalDateTime} lúc 00:00.
 * - {@code localTime.atDate(localDate)} → {@code LocalDateTime}.
 * - {@code localDateTime.toLocalDate()} và {@code toLocalTime()} - tách ngược ra.
 * - {@code localDateTime.atZone(zoneId)} → {@code ZonedDateTime} (bài t6). Đây là bước
 * "gắn đồng hồ vào một địa điểm", biến thời gian người thành thứ quy được ra thời gian máy.
 * - BẪY {@code atStartOfDay()}: bản không tham số trả về 00:00. Nhưng bản
 * {@code atStartOfDay(zoneId)} trả về {@code ZonedDateTime} và có thể KHÔNG phải 00:00 -
 * ở một số nước nửa đêm không tồn tại trong ngày đổi DST (Brazil từng như vậy). Trang t13
 * nhắc đúng cái bẫy này khi nói về code cũ dùng {@code GregorianCalendar} set giờ 00:00.
 * <p>
 * 6. TRUNCATEDTO - METHOD HAY BỊ BỎ QUÊN
 * - {@code LocalTime.now()} có cả nanosecond, nên hai giá trị gần như không bao giờ bằng nhau.
 * - {@code truncatedTo(ChronoUnit.SECONDS)} hoặc {@code MINUTES} cắt phần nhỏ hơn đi.
 * - Dùng khi: so sánh trong test, ghi xuống cột database chỉ lưu tới giây, gom nhóm theo phút.
 * - Cẩn thận chiều ngược lại: Postgres {@code timestamp} lưu tới microsecond, Java giữ tới
 * nanosecond. Ghi xuống rồi đọc lên có thể KHÔNG {@code equals()} với giá trị gốc.
 * Đây là nguyên nhân thật của nhiều test "chạy máy mình thì pass".
 * <p>
 * 7. LOCALTIME DÙNG THẬT Ở ĐÂU
 * - Giờ mở/đóng cửa, khung giờ ca trực, giờ chạy job hằng ngày, khung giờ khuyến mãi.
 * - Khoảng giờ VẮT QUA NỬA ĐÊM (22:00 - 02:00) là bẫy: so sánh
 * {@code now.isAfter(start) && now.isBefore(end)} sẽ luôn sai. Phải tách thành hai khoảng,
 * hoặc kiểm tra {@code start.isAfter(end)} để biết khoảng có vắt qua ngày không.
 * - {@code LocalTime} có các hằng tiện: {@code MIDNIGHT}, {@code NOON}, {@code MIN}, {@code MAX}.
 * Lưu ý {@code LocalTime.MAX} là 23:59:59.999999999 chứ không phải 24:00 - dùng nó làm biên
 * đóng của khoảng là nguồn lỗi lệch, nên dùng quy ước nửa mở như bài t4 mục 8.
 * <p>
 * 8. ĐỐI CHIẾU TYPESCRIPT
 * - JS không có kiểu "giờ thuần". Người ta hay lưu {@code "09:00"} dạng chuỗi, hoặc lưu số
 * phút từ nửa đêm. Cả hai đều mất kiểm tra hợp lệ: chuỗi {@code "25:70"} vẫn lưu được.
 * - {@code LocalTime.parse("25:70")} ném {@code DateTimeParseException} ngay.
 * - {@code LocalDateTime} ≈ {@code Temporal.PlainDateTime}; {@code LocalTime} ≈
 * {@code Temporal.PlainTime}.
 * - {@code truncatedTo} ≈ {@code Math.floor(ts / 1000) * 1000} mà người ta hay tự viết bên JS.
 * - Điểm khác lớn nhất: {@code new Date()} của JS LUÔN mang múi giờ, không có cách nào tạo
 * "ngày giờ trần". Java tách hai khái niệm ra thành hai kiểu khác nhau, nên compiler giúp
 * được. Đây là lý do {@code java.time} ít bug hơn hẳn.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Chữ "Local" trong {@code LocalDateTime} nghĩa là gì, KHÔNG phải nghĩa gì?
 * - Vì sao {@code LocalDateTime} không xác định được một điểm trên dòng thời gian?
 * - {@code LocalTime} có lưu thông tin daylight saving không?
 * - Kể hai tình huống NÊN dùng {@code LocalDateTime} và hai tình huống KHÔNG nên.
 * - Ghép {@code LocalDate} với giờ 9:00 bằng method nào? Tách ngược ra bằng method nào?
 * - {@code atStartOfDay()} và {@code atStartOfDay(zone)} khác nhau chỗ nào, bẫy ở đâu?
 * - {@code truncatedTo} giải quyết vấn đề gì trong test?
 * - Kiểm tra "bây giờ có nằm trong khung 22:00-02:00 không" thì sai ở đâu nếu viết ngây thơ?
 * - {@code LocalTime.MAX} là mấy giờ? Vì sao không nên dùng nó làm biên đóng?
 */
public class t5_DateAndTimeClasses {

    void main() {
        // TODO 1 (mục 3): In LocalDateTime.now() và Instant.now() cạnh nhau. Rồi thử
        //   so sánh chúng bằng isBefore() - xác nhận compiler KHÔNG cho. Viết một câu
        //   giải thích vì sao đây là tính năng chứ không phải hạn chế.
        //
        LocalTime moCua = LocalTime.of(9, 0);          // chi co GIO, lap lai moi ngay
        LocalDate homNay = LocalDate.of(2026, 10, 19); // chi co NGAY

        System.out.println("gio mo cua : " + moCua);
        System.out.println("hom nay    : " + homNay);
        System.out.println("ghep lai   : " + homNay.atTime(moCua));
        System.out.println("kieu gi?   : " + homNay.atTime(moCua).getClass().getSimpleName());
        // TODO 2 (mục 1): Dựng đồng hồ số: vòng lặp 5 lần, mỗi lần in LocalTime.now()
        //   đã truncatedTo(SECONDS), cách nhau 1 giây (Thread.sleep). Đây là ví dụ trang gốc gợi ý.
        //
        // TODO 3 (mục 5): Từ LocalDate hôm nay, dựng LocalDateTime lúc 9:00 bằng BA cách khác
        //   nhau (atTime overload khác nhau, atStartOfDay rồi plusHours, atDate từ LocalTime).
        //   Xác nhận ba kết quả equals().
        //
        // TODO 4 (mục 6): Tạo hai LocalDateTime.now() liên tiếp, in cả hai và so equals().
        //   Rồi truncatedTo(SECONDS) cả hai và so lại. Giải thích kết quả.
        //
        // TODO 5 (mục 6): In LocalDateTime.now() rồi in lại sau khi truncatedTo(MICROS).
        //   Đây là mô phỏng chuyện xảy ra khi ghi xuống Postgres rồi đọc lên.
        //
        // TODO 6 (mục 7): Viết hàm trongKhungGio(LocalTime now, LocalTime start, LocalTime end)
        //   xử lý ĐÚNG cả trường hợp khung vắt qua nửa đêm. Test với (23:30, 22:00, 02:00)
        //   và (10:00, 22:00, 02:00).
        //
        // TODO 7 (mục 7): In LocalTime.MIDNIGHT, NOON, MIN, MAX. Dự đoán MAX trước khi chạy.
        //
        // TODO 8 (mục 2): Dùng nhóm method plus/minus trên LocalDateTime: cộng 3 tuần,
        //   trừ 90 phút, cộng 1 tháng, trên cùng một mốc. In từng bước.
        //
        // TODO 9 (mục 4): Viết hai field trong một class Lich: LocalDateTime gioHen và
        //   Instant taoLuc. Viết comment một dòng cho mỗi field giải thích vì sao chọn kiểu đó.
        //
        // TODO 10 (mục 8): Thử LocalTime.parse("25:70") và LocalTime.parse("09:00").
        //   Dán lại exception của cái đầu, đọc message.
    }
}
