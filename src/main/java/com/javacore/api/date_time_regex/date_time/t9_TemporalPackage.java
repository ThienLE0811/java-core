package com.javacore.api.date_time_regex.date_time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * The Temporal Package
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 9/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Bài TRỪU TƯỢNG NHẤT section: đây là tầng nền mà tất cả các lớp đã học đứng lên.
 * - Một câu cho cả bài: {@code java.time.temporal} là bộ KHUNG ({@code Temporal},
 * {@code TemporalField}, {@code TemporalUnit}, {@code TemporalAmount}) - bạn hiếm khi khai
 * biến kiểu này, nhưng {@code TemporalAdjusters} thì dùng hằng tuần.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/temporal/">dev.java - The Temporal Package</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - THE TEMPORAL PACKAGE (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. TEMPORAL VÀ TEMPORALACCESSOR
 * - Package này "provides a collection of interfaces, classes, and enums that support date
 * and time code and, in particular, date and time calculations".
 * - {@code Temporal} "provides a framework for accessing temporal-based objects, and is
 * implemented by the temporal-based classes, such as {@code Instant}, {@code LocalDateTime},
 * and {@code ZonedDateTime}". Nó cho method cộng/trừ đơn vị thời gian, nhờ đó số học thời
 * gian nhất quán giữa mọi lớp.
 * - {@code TemporalAccessor} là "a read-only version of the {@code Temporal} interface".
 * - LỜI KHUYÊN QUAN TRỌNG, nguyên văn: "These interfaces are intended to be used at the
 * lowest level. Typical application code should declare variables and parameters in terms of
 * the concrete type, such as {@code LocalDate} or {@code ZonedDateTime}, and not in terms of
 * the {@code Temporal} interface."
 * - Cả hai được định nghĩa qua các FIELD, theo interface {@code TemporalField}.
 * <p>
 * 2. CHRONOFIELD - CÁC TRƯỜNG
 * - {@code ChronoField} là cài đặt cụ thể của {@code TemporalField}, cung cấp một bộ hằng
 * phong phú: {@code DAY_OF_WEEK}, {@code MINUTE_OF_HOUR}, {@code MONTH_OF_YEAR},
 * {@code CLOCK_HOUR_OF_DAY}, {@code NANO_OF_DAY}, {@code DAY_OF_YEAR}...
 * - Gặp một {@code Temporal} chưa rõ kiểu thì dùng
 * {@code TemporalAccessor.isSupported(TemporalField)} để hỏi xem nó có trường đó không.
 * - Trường riêng của lịch ISO nằm ở lớp {@code IsoFields}. Hai lớp khác cũng định nghĩa thêm
 * trường: {@code WeekFields} và {@code JulianFields}.
 * - Một field là tổ hợp của hai unit: "The field {@code ChronoField.DAY_OF_WEEK} is a
 * combination of {@code ChronoUnit.DAYS} and {@code ChronoUnit.WEEKS}."
 * <p>
 * 3. CHRONOUNIT - CÁC ĐƠN VỊ
 * - {@code ChronoUnit} cài {@code TemporalUnit}, cho bộ đơn vị chuẩn "from milliseconds to
 * millennia".
 * - CẢNH BÁO của trang gốc: "Note that not all {@code ChronoUnit} objects are supported by
 * all classes. For example, the {@code Instant} class does not support
 * {@code ChronoUnit.MONTHS} or {@code ChronoUnit.YEARS}." Đây chính là chuyện đã gặp ở bài t7.
 * - Kiểm tra trước bằng {@code isSupported(TemporalUnit)}.
 * - Các method số học của {@code Temporal} nhận tham số kiểu {@code TemporalAmount}, và
 * "{@code Period} and {@code Duration} classes implement the {@code TemporalAmount} interface"
 * - dẫn thẳng sang bài t10.
 * <p>
 * 4. TEMPORAL ADJUSTER
 * - {@code TemporalAdjuster} "provides methods that take a {@code Temporal} value and return
 * an adjusted value". Dùng được với mọi kiểu temporal.
 * - "If an adjuster is used with a {@code ZonedDateTime}, then a new date is computed that
 * preserves the original time and time zone values." Tức là nó chỉ đụng tới phần ngày.
 * - ADJUSTER CÓ SẴN: lớp {@code TemporalAdjusters} (CHÚ Ý SỐ NHIỀU) cho sẵn bộ adjuster tìm
 * ngày đầu/cuối tháng, ngày đầu/cuối năm, thứ Tư cuối cùng của tháng, thứ Ba đầu tiên sau
 * một ngày...
 * - "The predefined adjusters are defined as static methods and are designed to be used with
 * the static import statement."
 * - ADJUSTER TỰ VIẾT: tạo lớp cài {@code TemporalAdjuster} với method
 * {@code adjustInto(Temporal)}.
 * - Ví dụ {@code PaydayAdjuster} của trang gốc: trả về ngày trả lương kế tiếp, giả định
 * trả hai lần mỗi tháng vào ngày 15 và ngày cuối tháng; nếu rơi vào cuối tuần thì lùi về
 * thứ Sáu trước đó.
 * <p>
 * 5. TEMPORAL QUERY
 * - "A {@code TemporalQuery} can be used to retrieve information from a temporal-based object."
 * - QUERY CÓ SẴN: lớp {@code TemporalQueries} (cũng SỐ NHIỀU), hữu ích khi ứng dụng không
 * biết kiểu cụ thể của đối tượng temporal. Cũng là static method, cũng thiết kế để dùng với
 * static import.
 * - Ví dụ: query {@code precision} trả về {@code ChronoUnit} nhỏ nhất mà đối tượng đó biểu diễn được.
 * - QUERY TỰ VIẾT: tạo lớp cài {@code TemporalQuery} với method
 * {@code queryFrom(TemporalAccessor)}.
 * - Ví dụ của trang gốc: {@code FamilyVacations} so ngày truyền vào với các kỳ nghỉ đã lên lịch;
 * {@code FamilyBirthdays} có method {@code isFamilyBirthday()} so với vài ngày sinh.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. BỐN KHÁI NIỆM, ĐỪNG LẪN
 * - {@code Temporal} - một ĐIỂM thời gian, sửa được ({@code LocalDate}, {@code Instant}).
 * - {@code TemporalAmount} - một LƯỢNG thời gian ({@code Period}, {@code Duration}).
 * - {@code TemporalField} - một TRƯỜNG đọc được ({@code DAY_OF_WEEK}, {@code MONTH_OF_YEAR}).
 * - {@code TemporalUnit} - một ĐƠN VỊ đo ({@code DAYS}, {@code MONTHS}).
 * - Quan hệ: điểm {@code +} lượng {@code =} điểm. Điểm {@code -} điểm {@code =} lượng.
 * Trường thì ĐỌC ra số, đơn vị thì ĐO ra số.
 * - Phân biệt field với unit: {@code ChronoField.DAY_OF_MONTH} là "ngày thứ mấy trong tháng"
 * (một con số có vị trí); {@code ChronoUnit.DAYS} là "ngày" như một đơn vị đếm.
 * <p>
 * 7. TEMPORALADJUSTERS - PHẦN DÙNG NHIỀU NHẤT CỦA BÀI NÀY
 * - Bộ hay dùng: {@code firstDayOfMonth()}, {@code lastDayOfMonth()},
 * {@code firstDayOfNextMonth()}, {@code firstDayOfYear()}, {@code lastDayOfYear()},
 * {@code next(DayOfWeek)}, {@code nextOrSame(DayOfWeek)}, {@code previous(DayOfWeek)},
 * {@code previousOrSame(DayOfWeek)}, {@code firstInMonth(DayOfWeek)},
 * {@code lastInMonth(DayOfWeek)}, {@code dayOfWeekInMonth(int, DayOfWeek)}.
 * - CẶP DỄ NHẦM NHẤT: {@code next(FRIDAY)} luôn TIẾN tới thứ Sáu kế tiếp, kể cả hôm nay đã
 * là thứ Sáu. {@code nextOrSame(FRIDAY)} thì giữ nguyên nếu hôm nay đã là thứ Sáu.
 * Chọn sai là lệch đúng 7 ngày - bug im lặng, chỉ sai vào đúng một ngày trong tuần.
 * - Phân biệt với {@code date.with(DayOfWeek.FRIDAY)} ở bài t3 mục 8: cái đó đi tới thứ Sáu
 * TRONG CÙNG TUẦN ISO, nên có thể LÙI về quá khứ. Ba hành vi khác nhau, ba method khác nhau.
 * - {@code lastDayOfMonth()} là cách đúng để tính ngày cuối tháng, thay cho mọi mẹo tự chế
 * kiểu "ngày 1 tháng sau trừ 1".
 * <p>
 * 8. VIẾT ADJUSTER BẰNG LAMBDA - GỌN HƠN HẲN LỚP RIÊNG
 * - {@code TemporalAdjuster} là functional interface, nên không cần tạo lớp như trang gốc làm:
 * {@code TemporalAdjuster ngayLamViecKeTiep = t -> ...;}
 * - Có sẵn {@code TemporalAdjusters.ofDateAdjuster(UnaryOperator<LocalDate>)} khi chỉ cần
 * làm việc với {@code LocalDate} - không phải tự ép kiểu từ {@code Temporal}.
 * - Mẹo khi tự viết {@code adjustInto}: dùng {@code temporal.with(field, value)} để trả về
 * CÙNG KIỂU với đầu vào, đừng trả về {@code LocalDate} cứng - nếu không adjuster sẽ hỏng khi
 * ai đó dùng nó với {@code ZonedDateTime}.
 * - Adjuster là chỗ nên gói QUY TẮC NGHIỆP VỤ về ngày: ngày làm việc kế tiếp (bỏ qua cuối
 * tuần và ngày lễ), ngày đáo hạn, ngày chốt sổ. Viết một lần, dùng khắp nơi, test được riêng.
 * <p>
 * 9. TEMPORALQUERY - KHI NÀO THẬT SỰ CẦN
 * - Phần lớn code ứng dụng KHÔNG cần tự viết query - cứ gọi getter của lớp cụ thể là xong.
 * - Nó cần khi xử lý kết quả của {@code DateTimeFormatter.parse()} (trả về
 * {@code TemporalAccessor} chưa biết kiểu), hoặc khi viết thư viện nhận kiểu temporal bất kỳ.
 * - {@code TemporalQueries.zone()}, {@code offset()}, {@code precision()},
 * {@code localDate()}, {@code localTime()} - bộ này đủ cho hầu hết nhu cầu.
 * - Mẹo thực tế hay dùng: {@code formatter.parse(chuoi, LocalDate::from)} - tham số thứ hai
 * chính là một {@code TemporalQuery} viết dưới dạng method reference. Đây là cách parse sạch
 * nhất khi không chắc kiểu.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - Không có tương đương trực tiếp. date-fns cho một đống hàm rời ({@code nextFriday},
 * {@code lastDayOfMonth}) nhưng chúng chỉ nhận {@code Date}, không có khung chung.
 * - {@code TemporalAdjuster} ≈ một {@code (d: Date) => Date} bên TS, và
 * {@code date.with(adjuster)} ≈ gọi hàm đó. Khác biệt: Java có interface chung nên adjuster
 * của bạn dùng được với mọi kiểu temporal, kể cả lịch Nhật ở bài t12.
 * - Ý tưởng "gói quy tắc nghiệp vụ ngày tháng thành một object đặt tên được và test được"
 * là thứ nên mang ngược sang TS, không phụ thuộc ngôn ngữ.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Trang gốc khuyên khai biến kiểu {@code Temporal} hay kiểu cụ thể? Vì sao?
 * - {@code Temporal} khác {@code TemporalAccessor} ở điểm nào?
 * - Phân biệt {@code TemporalField} với {@code TemporalUnit}, cho ví dụ mỗi loại.
 * - Hai lớp nào cài {@code TemporalAmount}?
 * - Vì sao {@code Instant} không hỗ trợ {@code ChronoUnit.MONTHS}? Kiểm tra trước bằng gì?
 * - {@code TemporalAdjusters} (số nhiều) khác {@code TemporalAdjuster} (số ít) ở đâu?
 * - Phân biệt ba thứ: {@code next(FRIDAY)}, {@code nextOrSame(FRIDAY)},
 * {@code with(DayOfWeek.FRIDAY)}. Chạy vào đúng ngày thứ Sáu thì ba cái ra gì?
 * - Dùng adjuster với {@code ZonedDateTime} thì phần giờ và múi giờ có bị đụng không?
 * - Method nào phải cài khi tự viết adjuster? Còn query thì method nào?
 * - Khi nào thật sự cần {@code TemporalQuery}?
 */
public class t9_TemporalPackage {

    void main() {
        // TODO 1 (mục 2, 3): Với LocalDate, LocalTime, LocalDateTime, Instant, ZonedDateTime,
        //   in isSupported(ChronoField.MONTH_OF_YEAR) và isSupported(ChronoUnit.MONTHS).
        //   Lập bảng 5x2. Giải thích các ô false.
        //
        LocalDate homNay = LocalDate.now();

        // 1. Dùng TemporalAdjusters để tìm các mốc ngày đặc biệt
        LocalDate cuoiThang = homNay.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate thuSauToi = homNay.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        // 2. Dùng ChronoUnit để tính khoảng cách giữa 2 mốc thời gian
        long soNgayConLai = ChronoUnit.DAYS.between(homNay, cuoiThang);

        // In kết quả
        System.out.println("Hôm nay          : " + homNay);
        System.out.println("Ngày cuối tháng  : " + cuoiThang);
        System.out.println("Thứ Sáu kế tiếp  : " + thuSauToi);
        System.out.println("Còn lại đến hết tháng: " + soNgayConLai + " ngày");
        // TODO 2 (mục 2): Với LocalDateTime.now(), in giá trị của năm ChronoField khác nhau
        //   bằng get(field): DAY_OF_WEEK, DAY_OF_YEAR, CLOCK_HOUR_OF_DAY, NANO_OF_DAY,
        //   ALIGNED_WEEK_OF_MONTH. Đối chiếu CLOCK_HOUR_OF_DAY với HOUR_OF_DAY lúc 0h.
        //
        // TODO 3 (mục 7): Lấy LocalDate hôm nay, áp lần lượt 8 adjuster có sẵn
        //   (firstDayOfMonth, lastDayOfMonth, firstDayOfNextMonth, next(MONDAY),
        //   nextOrSame(MONDAY), previous(MONDAY), firstInMonth(MONDAY), lastInMonth(MONDAY)).
        //   In nhãn + kết quả từng cái.
        //
        // TODO 4 (mục 7): Chạy lại TODO 3 vào ĐÚNG ngày thứ Hai (dùng một LocalDate cố định
        //   là thứ Hai thay vì now()). Chỉ ra cặp next/nextOrSame lệch nhau 7 ngày.
        //
        // TODO 5 (mục 7): So ba cách lấy thứ Sáu: with(DayOfWeek.FRIDAY),
        //   with(TemporalAdjusters.next(FRIDAY)), with(TemporalAdjusters.nextOrSame(FRIDAY)).
        //   Chạy cho cả 7 ngày trong một tuần, lập bảng 7x3.
        //
        // TODO 6 (mục 4, 8): Viết PaydayAdjuster như trang gốc mô tả (trả lương ngày 15 và
        //   ngày cuối tháng, rơi vào cuối tuần thì lùi về thứ Sáu). Làm HAI bản:
        //   một bản là lớp cài TemporalAdjuster, một bản là lambda. So độ dài code.
        //
        // TODO 7 (mục 8): Test PaydayAdjuster với ZonedDateTime thay vì LocalDate.
        //   Nếu bản của bạn hỏng, sửa lại adjustInto() để giữ đúng kiểu đầu vào.
        //   Xác nhận giờ và múi giờ KHÔNG bị đụng, đúng như trang gốc nói.
        //
        // TODO 8 (mục 8): Viết adjuster ngayLamViecKeTiep(Set<LocalDate> ngayLe) bỏ qua
        //   thứ Bảy, Chủ nhật và danh sách ngày lễ truyền vào. Test với dịp Tết.
        //
        // TODO 9 (mục 5, 9): Dùng TemporalQueries.precision() trên LocalDate, LocalTime,
        //   LocalDateTime và Instant. In kết quả và giải thích.
        //
        // TODO 10 (mục 9): Parse "2026-10-19T10:15:30+07:00[Asia/Ho_Chi_Minh]" bằng
        //   DateTimeFormatter.ISO_ZONED_DATE_TIME.parse() KHÔNG truyền query - xem kiểu
        //   trả về. Rồi parse lại ba lần với LocalDate::from, LocalTime::from,
        //   ZonedDateTime::from và in kiểu thực tế mỗi lần.
        //
        // TODO 11 (mục 5): Viết TemporalQuery<Boolean> laNgayLe(Set<MonthDay>) trả về true
        //   nếu ngày truyền vào trùng một ngày lễ cố định. Gọi qua date.query(...).
        //
        // TODO 12 (mục 2): Dùng IsoFields.QUARTER_OF_YEAR và WeekFields.ISO lấy số quý và
        //   số tuần ISO của hôm nay. So với WeekFields.of(Locale.US) và giải thích chênh lệch.
    }
}
