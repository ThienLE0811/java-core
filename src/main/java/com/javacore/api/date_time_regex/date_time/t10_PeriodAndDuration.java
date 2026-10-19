package com.javacore.api.date_time_regex.date_time;

import java.time.*;

/**
 * Period and Duration
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 10/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Chuyển từ ĐIỂM thời gian sang LƯỢNG thời gian. Hai lớp cài {@code TemporalAmount}
 * (bài t9 mục 3) cộng một method thứ ba là {@code ChronoUnit.between()}.
 * - Một câu cho cả bài: {@code Duration} đếm GIÂY (thời gian máy), {@code Period} đếm
 * NĂM-THÁNG-NGÀY (thời gian người) - và "một ngày" theo hai cách đếm đó KHÔNG bằng nhau
 * vào ngày đổi DST.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/period-duration/">dev.java - Period and Duration</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - PERIOD AND DURATION (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. BA LỰA CHỌN
 * - "When you write code to specify an amount of time, use the class or method that best meets
 * your needs: the {@code Duration} class, {@code Period} class, or the
 * {@code ChronoUnit.between()} method."
 * <p>
 * 2. DURATION
 * - "A {@code Duration} measures an amount of time using time-based values (seconds,
 * nanoseconds)."
 * - "A {@code Duration} is most suitable in situations that measure machine-based time, such
 * as code that uses an {@code Instant} object."
 * - "A {@code Duration} object is measured in seconds or nanoseconds and does not use
 * date-based constructs such as years, months, and days".
 * - Có thể ÂM, nếu điểm cuối nằm trước điểm đầu.
 * - TÍNH CHẤT MẤU CHỐT: "A {@code Duration} is not connected to the timeline, in that it does
 * not track time zones or daylight saving time."
 * - Hệ quả trực tiếp: "A {@code Duration} of one day is exactly 24 hours long."
 * <p>
 * 3. CHRONOUNIT.BETWEEN
 * - "The {@code ChronoUnit} enum defines the units used to measure time."
 * - "The {@code ChronoUnit.between()} method is useful when you want to measure an amount of
 * time in a single unit of time only, such as days or seconds."
 * - "The {@code between()} method works with all temporal-based objects, but it returns the
 * amount in a single unit only."
 * <p>
 * 4. PERIOD
 * - "To define an amount of time with date-based values (years, months, days), use the
 * {@code Period} class."
 * - "The {@code Period} class provides various get methods, such as {@code getMonths()},
 * {@code getDays()}, and {@code getYears()}, so that you can extract the amount of time from
 * the period."
 * - CẢNH BÁO: "The total period of time is represented by all three units together: months,
 * days, and years." Tức là KHÔNG được đọc một getter rồi coi đó là tổng.
 * - "A {@code Period} of one day, when added to a {@code ZonedDateTime}, may vary according to
 * the time zone." Và "When you add a {@code Period} to a {@code ZonedDateTime}, the time
 * differences are observed."
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. BẢNG CHỌN NHANH
 * <p>
 * .  Nhu cầu                                   Dùng
 * .  timeout, TTL cache, độ trễ, đo hiệu năng  Duration
 * .  tuổi, thâm niên, kỳ hạn hợp đồng          Period
 * .  "còn bao nhiêu NGÀY nữa"                  ChronoUnit.DAYS.between
 * .  "cách nhau bao nhiêu giây"                ChronoUnit.SECONDS.between hoặc Duration
 * <p>
 * - Mẹo nhớ: {@code Duration} đi với {@code Instant}/{@code LocalTime}; {@code Period} đi với
 * {@code LocalDate}. Hai thế giới human time / machine time của bài t2 mục 1 lặp lại ở đây.
 * - {@code Period.between()} chỉ nhận {@code LocalDate}; {@code Duration.between()} nhận mọi
 * {@code Temporal} có phần giờ. Chính chữ ký method đã nói lên sự phân chia.
 * <p>
 * 6. "MỘT NGÀY" CÓ HAI NGHĨA - ĐÂY LÀ TRỌNG TÂM CỦA BÀI
 * - {@code Duration.ofDays(1)} = 86400 giây, luôn luôn.
 * - {@code Period.ofDays(1)} = "ngày lịch kế tiếp", có thể là 23 hoặc 25 giờ vào ngày đổi DST.
 * - Cộng cả hai vào cùng một {@code ZonedDateTime} ngay trước mốc đổi DST sẽ ra HAI kết quả
 * khác nhau. Đây là thí nghiệm bắt buộc phải tự chạy một lần.
 * - Ứng dụng thực tế: "gia hạn gói cước thêm 30 ngày" nên dùng {@code Period}, vì người dùng
 * hiểu theo lịch. "Token hết hạn sau 30 phút" phải dùng {@code Duration}.
 * - Cùng một logic giải thích vì sao {@code Instant} không cho cộng {@code MONTHS} (bài t7 mục 6).
 * <p>
 * 7. BẪY GETTER CỦA PERIOD - LỖI HAY GẶP NHẤT
 * - {@code Period.between(sinhNhat, homNay).getDays()} KHÔNG phải tổng số ngày sống.
 * Nó là phần DƯ ngày sau khi đã trừ hết năm và tháng.
 * - Ví dụ: {@code Period} "2 năm 3 tháng 5 ngày" thì {@code getDays()} trả về 5, không phải 826.
 * - Muốn TỔNG số ngày thì dùng {@code ChronoUnit.DAYS.between(a, b)}. Đây đúng là lý do
 * trang gốc tách {@code between()} thành lựa chọn thứ ba.
 * - Tính tuổi: {@code Period.between(ngaySinh, LocalDate.now()).getYears()} - cái này ĐÚNG,
 * vì tuổi chính là số năm trọn vẹn.
 * - {@code getMonths()} cũng là phần dư, {@code toTotalMonths()} mới là tổng tháng.
 * {@code Period} KHÔNG có {@code toTotalDays()} - cố tình, vì không có câu trả lời đúng nếu
 * chưa biết bắt đầu từ ngày nào.
 * <p>
 * 8. ĐỊNH DẠNG ISO-8601 CỦA HAI LỚP
 * - {@code Period.toString()} ra {@code P2Y3M5D}. {@code Duration.toString()} ra {@code PT20H30M}.
 * - Chữ {@code P} mở đầu là "Period"; chữ {@code T} ngăn phần ngày với phần giờ.
 * - Nên {@code P1D} là một NGÀY LỊCH còn {@code PT24H} là 24 GIỜ - chuẩn ISO cũng phân biệt
 * đúng như mục 6.
 * - Cả hai đều {@code parse()} ngược được từ chuỗi này. Rất tiện cho file cấu hình.
 * - Spring Boot đọc thẳng được: {@code @ConfigurationProperties} với field kiểu
 * {@code Duration} nhận cả {@code 30s}, {@code 5m}, {@code PT30S} nhờ
 * {@code @DurationUnit}. Đừng khai {@code long timeoutMs} nữa.
 * <p>
 * 9. API CỦA DURATION - NHỮNG METHOD NÊN BIẾT
 * - Tạo: {@code ofDays}, {@code ofHours}, {@code ofMinutes}, {@code ofSeconds},
 * {@code ofMillis}, {@code ofNanos}, {@code parse}.
 * - Đọc TỔNG (Java 8): {@code toDays()}, {@code toHours()}, {@code toMinutes()},
 * {@code getSeconds()}, {@code toMillis()}.
 * - Đọc PHẦN DƯ (Java 9+): {@code toHoursPart()}, {@code toMinutesPart()},
 * {@code toSecondsPart()}. Dùng bộ này để in "2h 15m 30s" mà không phải tự chia dư.
 * - Khác hẳn {@code Period}: getter của {@code Duration} là TỔNG, getter của {@code Period}
 * là PHẦN DƯ. Ngược nhau, dễ nhầm, nhớ kỹ.
 * - {@code Duration} là kiểu chuẩn cho timeout ở khắp nơi hiện đại: {@code HttpClient},
 * {@code Awaitility}, {@code RestClient} của Spring 6, {@code Caffeine} cache.
 * <p>
 * 10. NORMALIZED VÀ PHÉP TOÁN TRÊN PERIOD
 * - {@code Period.ofMonths(14).normalized()} ra {@code P1Y2M}. Chỉ gom năm với tháng, KHÔNG
 * đụng tới ngày - vì số ngày trong tháng không cố định.
 * - {@code Period.plus()} cộng từng thành phần riêng, KHÔNG tự chuẩn hóa:
 * {@code P1M.plus(P40D)} ra {@code P1M40D}, không ra {@code P2M10D}.
 * - Vì vậy {@code equals()} của {@code Period} so sánh TỪNG THÀNH PHẦN: {@code P1M} KHÔNG
 * bằng {@code P30D}. Hợp lý, nhưng hay làm test thất bại.
 * - {@code Duration} thì không có vấn đề này vì nó chỉ là một con số giây.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - JS không tách hai khái niệm: hiệu hai {@code Date} ra một {@code number} milli, tức là
 * luôn luôn kiểu {@code Duration}. Không có cách biểu diễn "1 tháng" như một lượng.
 * - Hệ quả: tính tuổi bên JS hay thấy kiểu {@code Math.floor(diff / 31557600000)} - sai lệch
 * vài ngày và không ai thấy. Java có {@code Period.between().getYears()} đúng tuyệt đối.
 * - date-fns có {@code differenceInDays}, {@code differenceInMonths} - từng hàm rời tương
 * ứng với {@code ChronoUnit.X.between()} của Java.
 * - {@code Temporal.Duration} của TC39 gom cả hai vai vào một lớp, nhưng vẫn phải truyền
 * {@code relativeTo} khi quy đổi đơn vị lịch - chính là thừa nhận cùng một vấn đề mà Java
 * giải bằng cách tách hai lớp.
 * - Chuỗi {@code "PT30S"} là chuẩn ISO-8601 nên dùng chung được giữa Java và JS, hợp cho
 * trường kiểu duration trong JSON API.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba cách biểu diễn lượng thời gian là gì, mỗi cách dùng khi nào?
 * - {@code Duration} đo bằng đơn vị nào? Nó có biết gì về múi giờ không?
 * - "{@code Duration} của một ngày dài đúng bao nhiêu" - câu nguyên văn của trang gốc là gì?
 * - {@code Period.ofDays(1)} cộng vào {@code ZonedDateTime} ngày đổi DST ra khác gì
 * {@code Duration.ofDays(1)}?
 * - {@code Period.between(a,b).getDays()} có phải tổng số ngày không? Muốn tổng thì dùng gì?
 * - Getter của {@code Period} trả về tổng hay phần dư? Của {@code Duration} thì sao?
 * - Vì sao {@code Period} không có {@code toTotalDays()}?
 * - {@code P1D} khác {@code PT24H} ở đâu?
 * - {@code Period.ofMonths(1).equals(Period.ofDays(30))} trả về gì, vì sao?
 * - {@code normalized()} làm gì, và cố tình KHÔNG làm gì?
 * - Khai timeout trong Spring Boot config nên dùng kiểu gì thay cho {@code long}?
 */
public class t10_PeriodAndDuration {

    void main() {
        // TODO 1 (mục 2, 4): In toString() của Period.of(2,3,5) và Duration.ofHours(20).
        //   Rồi parse ngược lại từ chuỗi đó. Giải thích vai trò chữ P và chữ T.
        //
        // 1. PERIOD: Đo lượng thời gian con người (Năm - Tháng - Ngày)
        LocalDate ngayVaoCongTy = LocalDate.of(2023, 1, 10);
        LocalDate homNay = LocalDate.of(2026, 10, 19);

        Period thamNien = Period.between(ngayVaoCongTy, homNay);
        System.out.println("Thâm niên: "
                + thamNien.getYears() + " năm "
                + thamNien.getMonths() + " tháng "
                + thamNien.getDays() + " ngày"); // Thâm niên: 3 năm 9 tháng 9 ngày

        // 2. DURATION: Đo lượng thời gian máy tính (Giờ - Phút - Giây)
        LocalTime gioVaoCa = LocalTime.of(8, 0);
        LocalTime gioTanCa = LocalTime.of(17, 30);

        Duration thoiGianLam = Duration.between(gioVaoCa, gioTanCa);
        System.out.println("Thời gian làm việc: " + thoiGianLam.toHours() + " tiếng"); // 9 tiếng
        System.out.println("Tổng số phút      : " + thoiGianLam.toMinutes() + " phút"); // 570 phút
        // TODO 2 (mục 6 - THÍ NGHIỆM CHÍNH CỦA BÀI): Lấy ZonedDateTime 12:00 ngày TRƯỚC
        //   mốc đổi DST mùa xuân của Europe/Berlin. Cộng Period.ofDays(1) và
        //   Duration.ofDays(1). In hai kết quả. Chúng lệch nhau 1 tiếng - ghi lại kết luận.
        //
        // TODO 3 (mục 6): Làm lại TODO 2 với Asia/Ho_Chi_Minh. Xác nhận hai kết quả giống
        //   nhau và giải thích vì sao (gợi ý: bài t6 - Việt Nam không có DST).
        //
        // TODO 4 (mục 7 - BẪY CHÍNH): Với ngày sinh của mình, in
        //   Period.between(sinh, now).getDays() và ChronoUnit.DAYS.between(sinh, now).
        //   Dán hai số cạnh nhau. Viết một câu giải thích vì sao chúng khác nhau hoàn toàn.
        //
        // TODO 5 (mục 7): In đủ getYears(), getMonths(), getDays(), toTotalMonths() của
        //   cùng Period đó. Chỉ ra cái nào là tổng, cái nào là phần dư.
        //
        // TODO 6 (mục 7): Viết hàm tuoi(LocalDate ngaySinh) trả về int. Test với ngày sinh
        //   29/02 và ngày hiện tại là 28/02 của một năm không nhuận - xem kết quả có hợp lý không.
        //
        // TODO 7 (mục 9): Với Duration.ofSeconds(8130), in toHours(), toMinutes(),
        //   toHoursPart(), toMinutesPart(), toSecondsPart(). Rồi viết hàm format thành
        //   chuỗi "2h 15m 30s".
        //
        // TODO 8 (mục 9): Làm cùng việc TODO 7 nhưng CHỈ dùng API Java 8 (tự chia dư).
        //   So độ dài hai cách và nhận xét.
        //
        // TODO 9 (mục 3): Dùng ChronoUnit.between() đo khoảng giữa hai LocalDateTime theo
        //   năm đơn vị khác nhau (YEARS, MONTHS, DAYS, HOURS, MINUTES). In bảng.
        //   Thử thêm với hai Instant và xem đơn vị nào ném exception.
        //
        // TODO 10 (mục 10): Kiểm chứng Period.ofMonths(1).equals(Period.ofDays(30)) và
        //   Period.ofMonths(14).normalized(). Rồi thử Period.ofMonths(1).plus(Period.ofDays(40))
        //   và xem nó có tự gom không.
        //
        // TODO 11 (mục 5): Cho năm tình huống, chọn Duration hay Period hay between():
        //   (a) token hết hạn sau 15 phút, (b) gói cước 30 ngày, (c) thâm niên nhân viên,
        //   (d) thời gian chạy một query, (e) còn bao nhiêu ngày tới Tết.
        //
        // TODO 12 (mục 8): Nếu có dự án Spring Boot, khai một @ConfigurationProperties có
        //   field Duration và thử đặt giá trị "30s", "5m", "PT1H30M" trong application.yml.
        //   Xác nhận cả ba đều đọc được.
    }
}
