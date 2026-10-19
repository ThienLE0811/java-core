package com.javacore.api.date_time_regex.date_time;

import java.time.LocalDate;

/**
 * Date
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 4/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Bài đầu tiên đi vào lớp cụ thể. Bốn lớp CHỈ CÓ NGÀY, không có giờ:
 * {@code LocalDate}, {@code YearMonth}, {@code MonthDay}, {@code Year}.
 * - Một câu cho cả bài: khi bài toán không cần giờ thì đừng mang giờ theo - ba lớp
 * {@code YearMonth}/{@code MonthDay}/{@code Year} tồn tại để diễn đạt chính xác "chỉ cần
 * tới mức này thôi".
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/date/">dev.java - Date</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - DATE (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. BỐN LỚP CHỈ CHỨA NGÀY
 * - Date-Time API cho bốn lớp dành riêng cho thông tin ngày: {@code LocalDate},
 * {@code YearMonth}, {@code MonthDay}, {@code Year}.
 * <p>
 * 2. LỚP LOCALDATE
 * - (The LocalDate Class)
 * - Biểu diễn "a year-month-day in the ISO calendar and is useful for representing a date
 * without a time".
 * - Dùng cho các mốc như ngày sinh, ngày cưới - những thứ cùng một ngày lịch ở mọi nơi.
 * - Tạo bằng {@code of}, sửa bằng {@code with}, đọc bằng các getter.
 * - {@code getDayOfWeek()} trả về thứ của ngày đó (chính là enum {@code DayOfWeek} bài t3).
 * - Trang gốc nhắc tới {@code TemporalAdjuster} để lấy những ngày kiểu "thứ Tư đầu tiên sau
 * ngày X" - học kỹ ở bài t9.
 * <p>
 * 3. LỚP YEARMONTH
 * - (The YearMonth Class)
 * - Biểu diễn một tháng của một năm cụ thể.
 * - {@code lengthOfMonth()} cho biết tháng đó có bao nhiêu ngày, ĐÃ tính tới năm nhuận.
 * Đây là điểm khác {@code Month.maxLength()} ở bài t3: {@code Month} không biết năm nên
 * chỉ trả được khoảng 28-29.
 * <p>
 * 4. LỚP MONTHDAY
 * - (The MonthDay Class)
 * - Biểu diễn "the day of a particular month, such as New Year's Day on January 1" - một
 * ngày lặp lại hằng năm, không gắn năm cụ thể.
 * - {@code isValidYear(int)} kiểm tra ngày đó có hợp lệ trong một năm cho trước không.
 * Ví dụ của trang gốc: 29/02 với năm 2010 trả về {@code false}.
 * <p>
 * 5. LỚP YEAR
 * - (The Year Class)
 * - Biểu diễn một năm.
 * - {@code isLeap()} cho biết có phải năm nhuận không. Ví dụ trang gốc: 2012 trả về {@code true}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. BA CÁCH TẠO LOCALDATE - BIẾT CẢ BA
 * - {@code LocalDate.of(2026, 10, 19)} - rõ ràng nhất. Có overload nhận {@code Month} enum:
 * {@code LocalDate.of(2026, Month.OCTOBER, 19)}, nên dùng bản này khi tháng là hằng số.
 * - {@code LocalDate.parse("2026-10-19")} - mặc định dùng định dạng ISO, không cần formatter.
 * - {@code LocalDate.now()} - LẤY NGÀY THEO MÚI GIỜ HỆ THỐNG. Đây là chỗ cần cẩn thận:
 * server chạy UTC thì lúc 1h sáng giờ Việt Nam, {@code now()} vẫn trả về NGÀY HÔM TRƯỚC.
 * Ở production nên dùng {@code LocalDate.now(zoneId)} hoặc {@code LocalDate.now(clock)}.
 * - {@code of()} ném {@code DateTimeException} nếu ngày không tồn tại ({@code of(2026, 2, 30)}).
 * Nó KHÔNG tự cuộn sang 2/3 như {@code Calendar} cũ hay như {@code new Date()} của JS.
 * <p>
 * 7. PLUSMONTHS VÀ QUY TẮC CẮT NGÀY - HÀNH VI PHẢI THUỘC
 * - {@code LocalDate.of(2026, 1, 31).plusMonths(1)} ra 28/02/2026, KHÔNG phải 03/03.
 * Quy tắc: nếu ngày không tồn tại ở tháng đích thì CẮT về ngày cuối tháng.
 * - Hệ quả quan trọng: phép cộng tháng KHÔNG có tính kết hợp ngược.
 * {@code plusMonths(1).minusMonths(1)} từ 31/01 ra 28/01, không quay lại 31/01.
 * - Chuyện này gây bug thật ở tính kỳ hạn hợp đồng, kỳ trả góp, ngày gia hạn thuê bao.
 * Cách xử lý: luôn tính từ NGÀY GỐC ({@code ngayDau.plusMonths(n)}) chứ đừng cộng dồn
 * từng tháng một.
 * <p>
 * 8. SO SÁNH VÀ KHOẢNG NGÀY
 * - {@code isBefore}, {@code isAfter}, {@code isEqual} - dùng ba cái này, đừng dùng
 * {@code compareTo() < 0} vì kém rõ.
 * - {@code equals()} so sánh cả kiểu lẫn giá trị; {@code isEqual()} so sánh trên dòng thời gian.
 * Với {@code LocalDate} hai cái cho cùng kết quả, nhưng thói quen dùng {@code isEqual} giúp
 * nhất quán khi sang {@code ChronoLocalDate} và các lớp lịch khác.
 * - Java SE 9 thêm {@code datesUntil(endExclusive)} trả về {@code Stream<LocalDate>} - rất
 * hợp với bước 20 (Stream API) đã học. Ví dụ đếm ngày làm việc trong tháng chỉ vài dòng.
 * - Khoảng ngày trong nghiệp vụ nên theo quy ước NỬA MỞ {@code [start, end)}: đỡ hẳn lỗi
 * lệch một ngày khi ghép các khoảng liền nhau. {@code datesUntil} cũng theo quy ước này.
 * <p>
 * 9. YEARMONTH DÙNG THẬT Ở ĐÂU
 * - Kỳ báo cáo, kỳ lương, kỳ hóa đơn. Thay vì {@code (int nam, int thang)} trong chữ ký
 * method - vốn dễ truyền nhầm thứ tự mà compiler không bắt được - truyền một {@code YearMonth}.
 * - {@code atDay(1)} và {@code atEndOfMonth()} cho ngay khoảng ngày của kỳ đó, không phải
 * tự tính số ngày cuối tháng.
 * - {@code YearMonth.parse("2026-10")} đọc thẳng định dạng của query param
 * {@code ?period=2026-10}, dùng được trực tiếp làm kiểu tham số trong Spring MVC.
 * <p>
 * 10. NĂM NHUẬN - ĐỪNG TỰ VIẾT LẠI
 * - Luật đầy đủ: chia hết 4, trừ khi chia hết 100, trừ khi chia hết 400. Rất nhiều người
 * viết thiếu vế thứ ba.
 * - Đã có sẵn ba chỗ: {@code Year.isLeap(2026)} (static), {@code year.isLeap()} (instance),
 * {@code localDate.isLeapYear()}. Dùng chúng.
 * - Chuyện có thật: bug 29/02 xuất hiện lại mỗi bốn năm trong các hệ thống tự tính ngày.
 * Nếu code có số 365 hoặc số 4 viết cứng, đó là dấu hiệu sắp hỏng.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - JS không có khái niệm "ngày thuần". Phổ biến là lưu {@code "2026-10-19"} dạng
 * {@code string} rồi tự parse - mất hết kiểm tra hợp lệ.
 * - Hoặc dùng {@code new Date("2026-10-19")}, nhưng chuỗi chỉ-có-ngày bị hiểu là UTC còn
 * chuỗi có giờ lại hiểu theo giờ địa phương. Chính sự không nhất quán này làm "ngày lệch
 * một hôm" là bug phổ biến nhất của JS.
 * - {@code LocalDate} của Java không có khái niệm múi giờ nên không dính bug đó.
 * - {@code LocalDate} ≈ {@code Temporal.PlainDate} của TC39; {@code YearMonth} ≈
 * {@code Temporal.PlainYearMonth}; {@code MonthDay} ≈ {@code Temporal.PlainMonthDay}.
 * Bộ ba này của JS mượn thẳng từ Java.
 * - {@code datesUntil()} ≈ sinh mảng bằng vòng lặp rồi {@code map}, nhưng Java lười
 * (lazy stream) nên khoảng một triệu ngày vẫn không tốn bộ nhớ.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Kể bốn lớp chỉ-có-ngày và một tình huống nghiệp vụ cho mỗi lớp.
 * - {@code LocalDate.of(2026, 2, 30)} cho ra gì? Khác gì {@code new Date(2026, 1, 30)} của JS?
 * - {@code LocalDate.of(2026,1,31).plusMonths(1)} ra ngày nào? Cộng rồi trừ lại có về chỗ cũ không?
 * - Vì sao tính kỳ hạn phải cộng từ ngày gốc thay vì cộng dồn từng tháng?
 * - {@code LocalDate.now()} trên server chạy UTC lúc 1h sáng giờ VN trả về ngày nào?
 * Sửa bằng cách nào?
 * - {@code Month.maxLength()} khác {@code YearMonth.lengthOfMonth()} ở điểm nào?
 * - {@code MonthDay.of(2,29).isValidYear(2010)} trả về gì, vì sao?
 * - Luật năm nhuận đầy đủ gồm mấy vế? Gọi method nào thay vì tự viết?
 * - {@code datesUntil()} bao gồm hay không bao gồm ngày cuối? Vì sao quy ước đó tốt hơn?
 */
public class t4_DateClasses {

    void main() {
        // TODO 1 (mục 6): Tạo cùng một ngày 19/10/2026 bằng cả ba cách: of(int,int,int),
        //   of(int,Month,int), parse("2026-10-19"). Xác nhận cả ba equals() nhau.
        //
        LocalDate ngaySinh = LocalDate.of(1995, 10, 19);

        System.out.println("ngay sinh     : " + ngaySinh);
        System.out.println("thu           : " + ngaySinh.getDayOfWeek());
        System.out.println("nam nhuan?    : " + ngaySinh.isLeapYear());
        System.out.println("sinh nhat 2026: " + ngaySinh.withYear(2026).getDayOfWeek());
        // TODO 2 (mục 6): Gọi LocalDate.of(2026, 2, 30). Dán lại exception và đọc kỹ message
        //   - nó nói rõ sai ở đâu, so với việc JS im lặng cuộn sang 2/3.
        //
        // TODO 3 (mục 7): Chạy LocalDate.of(2026,1,31).plusMonths(1) rồi .minusMonths(1).
        //   In cả ba bước. Viết một câu kết luận về việc phép cộng tháng không đảo ngược được.
        //
        // TODO 4 (mục 7): Mô phỏng kỳ trả góp 12 tháng bắt đầu 31/01/2026 theo HAI cách:
        //   (a) cộng dồn mỗi vòng plusMonths(1), (b) ngayGoc.plusMonths(i) với i = 1..12.
        //   In hai danh sách cạnh nhau và chỉ ra chỗ lệch.
        //
        // TODO 5 (mục 2): Với ngày sinh của mình, in getDayOfWeek(), getDayOfYear(),
        //   lengthOfYear(), isLeapYear(). Rồi dùng getDisplayName của bài t3 in thứ bằng tiếng Việt.
        //
        // TODO 6 (mục 8): Dùng datesUntil() đếm số ngày KHÔNG phải thứ Bảy/Chủ nhật trong
        //   tháng này. Viết bằng stream, một biểu thức.
        //
        // TODO 7 (mục 3, 9): Viết method baoCao(YearMonth ky) in ngày đầu kỳ, ngày cuối kỳ
        //   và số ngày trong kỳ. Gọi với YearMonth.parse("2024-02") và "2026-02" để thấy
        //   lengthOfMonth() khác nhau.
        //
        // TODO 8 (mục 4): Tạo MonthDay 29/02, gọi isValidYear() cho 2023, 2024, 2100.
        //   Dự đoán trước ba kết quả rồi chạy kiểm chứng (chú ý 2100).
        //
        // TODO 9 (mục 10): Viết hàm namNhuanTuChe(int) theo trí nhớ, KHÔNG tra, rồi so kết quả
        //   với Year.isLeap() cho khoảng 1900..2400. In ra các năm hai bên bất đồng.
        //
        // TODO 10 (mục 5, 9): Viết hàm nhận LocalDate, trả về YearMonth của kỳ đó
        //   (gợi ý: YearMonth.from). Đây là ví dụ của tiền tố "from" ở bài t1 mục 4.
    }
}
