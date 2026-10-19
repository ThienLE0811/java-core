package com.javacore.api.date_time_regex.date_time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Parsing and Formatting
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 8/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Bài về BIÊN GIỚI của hệ thống: chỗ ngày giờ đi vào từ request, form, file CSV, và đi ra
 * tới giao diện, báo cáo, API response.
 * - Một câu cho cả bài: {@code DateTimeFormatter} bất biến và thread-safe nên PHẢI khai thành
 * hằng {@code static final} - ai từng dùng {@code SimpleDateFormat} sẽ thấy đây là khác biệt lớn nhất.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/parsing-formatting/">dev.java - Parsing and Formatting</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - PARSING AND FORMATTING (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. HAI CHIỀU, MỘT CƠ CHẾ
 * - "The temporal-based classes in the Date-Time API provide {@code parse()} methods for
 * parsing a string that contains date and time information."
 * - Chiều ngược lại là {@code format()}.
 * - Quy trình giống nhau cho cả hai chiều: đưa pattern cho {@code DateTimeFormatter} để tạo
 * formatter, rồi truyền formatter đó vào {@code parse()} hoặc {@code format()}.
 * <p>
 * 2. VỀ DATETIMEFORMATTER
 * - "{@code DateTimeFormatter} class provides numerous predefined formatters, or you can
 * define your own."
 * - CÂU QUAN TRỌNG NHẤT CỦA BÀI: "The {@code DateTimeFormatter} class is both immutable and
 * thread-safe; it can (and should) be assigned to a static constant."
 * - Chữ "should" là của trang gốc, không phải lời khuyên thêm.
 * <p>
 * 3. NGOẠI LỆ
 * - "The {@code parse()} and the {@code format()} methods throw an exception if a problem
 * occurs during the conversion process."
 * - Code parse bắt {@code DateTimeParseException}.
 * - Code format bắt {@code DateTimeException}.
 * <p>
 * 4. PARSING
 * - Bản MỘT THAM SỐ {@code parse(CharSequence)}: ví dụ của {@code LocalDate} dùng formatter
 * {@code ISO_LOCAL_DATE} có sẵn, không cần khai gì thêm.
 * - Bản HAI THAM SỐ {@code parse(CharSequence, DateTimeFormatter)}: chỉ định formatter khác.
 * - Ví dụ pattern của trang gốc:
 * + {@code MMM d yyyy} nhận được "Jan 3 2003" và cả "Mar 23 1994" - một chữ {@code d}
 * nên ngày có thể một hoặc hai chữ số.
 * + {@code MMM dd yyyy} BẮT BUỘC hai chữ số cho ngày, ví dụ "Jun 03 2003".
 * <p>
 * 5. FORMATTING
 * - "{@code ZonedDateTime.format()} method converts a temporal-based object to a string
 * representation using the specified format."
 * - Ví dụ pattern: {@code MMM d yyy hh:mm a} - có giờ, phút và a.m./p.m.
 * - Các lớp khác cũng có {@code format(DateTimeFormatter)}: {@code OffsetDateTime},
 * {@code OffsetTime}, {@code LocalDateTime}, {@code LocalDate}, {@code LocalTime}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. VÌ SAO "STATIC CONSTANT" LẠI QUAN TRỌNG ĐẾN THẾ
 * - {@code SimpleDateFormat} của API cũ KHÔNG thread-safe. Khai nó thành field dùng chung
 * trong một {@code @Service} của Spring (vốn là singleton) là lỗi production kinh điển:
 * chạy tốt lúc ít tải, ra kết quả sai hoặc ném exception lạ khi nhiều request đồng thời.
 * - Bug này cực khó tái hiện vì nó phụ thuộc thời điểm. Nhiều team sống chung với nó nhiều năm.
 * - {@code DateTimeFormatter} bất biến nên không có vấn đề đó. Tạo một lần, dùng mãi mãi.
 * - Dấu hiệu code cũ cần sửa: {@code new SimpleDateFormat(...)} bên trong method, hoặc tệ hơn
 * là {@code private SimpleDateFormat sdf = ...} ở cấp class.
 * <p>
 * 7. BA CHỮ CÁI PATTERN GÂY BUG NHIỀU NHẤT
 * - {@code yyyy} là NĂM LỊCH. {@code YYYY} là "week-based year" theo chuẩn ISO. Chúng khác
 * nhau ở vài ngày cuối/đầu năm: 31/12/2019 với {@code YYYY} ra 2020. Đây là bug nổi tiếng,
 * xuất hiện lại mỗi dịp cuối năm.
 * - {@code mm} là PHÚT. {@code MM} là THÁNG. Viết {@code dd/mm/yyyy} là ra phút chứ không
 * phải tháng.
 * - {@code DD} là ngày trong NĂM (1-366), {@code dd} là ngày trong tháng. Dùng nhầm thì sau
 * ngày 31 trong năm sẽ ném exception.
 * - {@code hh} là giờ 12 (1-12, cần có {@code a} đi kèm mới đủ nghĩa), {@code HH} là giờ 24.
 * Pattern {@code hh:mm} không có {@code a} là mất thông tin sáng/chiều.
 * - Mẹo nhớ: chữ HOA thường là đơn vị LỚN hơn hoặc lạ hơn. Gặp chữ hoa thì dừng lại kiểm tra.
 * <p>
 * 8. FORMATTER CÓ SẴN - DÙNG TRƯỚC KHI TỰ VIẾT PATTERN
 * - {@code ISO_LOCAL_DATE} ({@code 2026-10-19}), {@code ISO_LOCAL_DATE_TIME},
 * {@code ISO_INSTANT}, {@code ISO_OFFSET_DATE_TIME}.
 * - {@code ISO_OFFSET_DATE_TIME} chính là định dạng của JSON API hiện đại và là cái Jackson
 * dùng. {@code RFC_1123_DATE_TIME} là định dạng của HTTP header.
 * - {@code DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)} cho định dạng theo locale -
 * đúng thứ cần ở tầng hiển thị, thay vì tự viết pattern cho từng ngôn ngữ.
 * - Nguyên tắc: giao tiếp MÁY-MÁY luôn dùng ISO. Pattern tự viết chỉ dành cho hiển thị cho
 * người, hoặc để đọc file từ hệ thống cũ.
 * <p>
 * 9. LOCALE VÀ ZONE TRÊN FORMATTER
 * - {@code DateTimeFormatter.ofPattern("MMM d yyyy")} dùng locale MẶC ĐỊNH của JVM. Với
 * locale Việt, {@code MMM} ra "thg 1" chứ không phải "Jan" - và pattern đó sẽ parse hỏng
 * chuỗi tiếng Anh.
 * - Luôn viết rõ: {@code ofPattern("MMM d yyyy", Locale.ENGLISH)}. Đây là lỗi hay gặp nhất
 * khi "code chạy máy mình mà hỏng trên server".
 * - {@code withZone(zoneId)} cần thiết khi format một {@code Instant} bằng pattern có
 * ngày/giờ - không có zone thì ném {@code UnsupportedTemporalTypeException}, vì
 * {@code Instant} không biết "ngày" là gì (bài t7 mục 4).
 * - {@code withResolverStyle(ResolverStyle.STRICT)} bật kiểm tra nghiêm. Mặc định là
 * {@code SMART}, vốn âm thầm sửa 31/02 thành 28/02. Với dữ liệu người dùng nhập thì
 * {@code STRICT} an toàn hơn. Lưu ý: {@code STRICT} đòi dùng {@code uuuu} thay cho
 * {@code yyyy} vì {@code yyyy} là "year-of-era" cần thêm kỷ nguyên.
 * <p>
 * 10. PARSE LỖI - XỬ LÝ THẾ NÀO Ở TẦNG SPRING MVC
 * - {@code DateTimeParseException} là {@code RuntimeException}, không bị bắt buộc bắt.
 * Nhưng ở biên giới hệ thống thì PHẢI bắt, nếu không nó thành HTTP 500 cho một lỗi vốn là 400.
 * - {@code e.getParsedString()} và {@code e.getErrorIndex()} cho biết chuỗi nào, sai ở vị trí
 * nào - đưa vào thông báo lỗi cho client.
 * - Spring MVC có cách sạch hơn: khai thẳng kiểu {@code LocalDate} ở tham số controller và
 * gắn {@code @DateTimeFormat(iso = ISO.DATE)}. Spring tự parse và tự trả 400 khi sai.
 * - TUYỆT ĐỐI không nuốt exception rồi trả về {@code null} hay {@code LocalDate.now()}.
 * Ngày sai lặng lẽ chui vào database là loại bug tốn nhiều tiền nhất.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - {@code new Date("19/10/2026")} của JS tùy trình duyệt mà ra {@code Invalid Date} hoặc một
 * ngày nào đó. Không ném exception - phải tự kiểm tra {@code isNaN(d.getTime())}.
 * Java ném {@code DateTimeParseException} ngay, không thể bỏ sót.
 * - {@code toLocaleDateString(locale, options)} ≈ {@code ofLocalizedDate(FormatStyle)}.
 * - JS không có khái niệm pattern chuẩn hóa như {@code yyyy-MM-dd}; thư viện date-fns tự
 * định nghĩa bộ pattern riêng, gần giống Java nhưng KHÔNG giống hệt (date-fns dùng
 * {@code yyyy} và {@code YYYY} với đúng cái bẫy ở mục 7 - nên bug này có ở cả hai hệ).
 * - {@code DateTimeFormatter} là hằng dùng lại được; bên JS {@code Intl.DateTimeFormat} cũng
 * nên tạo một lần vì khởi tạo khá tốn - cùng một bài học về hiệu năng.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code DateTimeFormatter} có thread-safe không? Trang gốc khuyên khai nó thế nào?
 * - {@code SimpleDateFormat} hỏng ở chỗ nào khi để làm field của một {@code @Service}?
 * - Phân biệt {@code yyyy} với {@code YYYY}; cho một ngày cụ thể mà hai cái ra khác nhau.
 * - Phân biệt {@code mm} với {@code MM}; {@code dd} với {@code DD}; {@code hh} với {@code HH}.
 * - Pattern {@code MMM d yyyy} khác {@code MMM dd yyyy} ở điểm nào khi parse?
 * - Parse hỏng thì ném exception gì, format hỏng thì ném gì?
 * - Format một {@code Instant} bằng pattern có ngày thì thiếu gì, thêm bằng method nào?
 * - {@code ResolverStyle} mặc định là gì, nó âm thầm làm gì với 31/02?
 * - Vì sao phải truyền {@code Locale} tường minh vào {@code ofPattern}?
 * - Trong Spring MVC, cách sạch nhất để nhận một {@code LocalDate} từ query param là gì?
 */
public class t8_ParsingAndFormatting {

    // TODO 0 (mục 2, 6): Khai ở đây các DateTimeFormatter dùng chung của cả bài,
    //   dưới dạng private static final, mỗi cái có Locale tường minh.
    //   Đây là bài tập quan trọng nhất về thói quen, làm trước khi làm các TODO khác.
    static final DateTimeFormatter KIEU_VN = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    void main() {
        // TODO 1 (mục 4): Parse "2026-10-19" bằng LocalDate.parse() KHÔNG truyền formatter.
        //   Giải thích nó dùng formatter nào (tra javadoc để xác nhận, đừng đoán).
        //
        LocalDate ngay = LocalDate.of(2026, 10, 19);

        String chuoi = ngay.format(KIEU_VN);                // doi tuong -> chuoi
        LocalDate docLai = LocalDate.parse(chuoi, KIEU_VN); // chuoi -> doi tuong

        System.out.println("format : " + chuoi);
        System.out.println("parse  : " + docLai);
        System.out.println("ve dung cho cu? " + ngay.equals(docLai));
        // TODO 2 (mục 4): Dựng formatter "MMM d yyyy" và parse cả "Jan 3 2003" lẫn "Mar 23 1994".
        //   Rồi dựng "MMM dd yyyy" và thử parse "Jun 3 2003". Dán lại exception.
        //
        // TODO 3 (mục 9): Làm lại TODO 2 nhưng KHÔNG truyền Locale.ENGLISH, sau khi chạy
        //   với -Duser.language=vi. Ghi lại chuyện gì xảy ra. Đây là bug "chạy máy mình thì ổn".
        //
        // TODO 4 (mục 7): In LocalDate.of(2019,12,31) bằng pattern "yyyy-MM-dd" và "YYYY-MM-dd".
        //   Dán hai output cạnh nhau. Làm thêm cho 29/12/2019 và 01/01/2021.
        //
        // TODO 5 (mục 7): Format 14:30 bằng "hh:mm" và "HH:mm". Rồi thêm "a" vào cái đầu.
        //   Kết luận về việc dùng hh mà thiếu a.
        //
        // TODO 6 (mục 8): In cùng một ZonedDateTime bằng năm formatter có sẵn:
        //   ISO_LOCAL_DATE_TIME, ISO_OFFSET_DATE_TIME, ISO_ZONED_DATE_TIME, ISO_INSTANT,
        //   RFC_1123_DATE_TIME. Ghi chú cái nào dùng cho HTTP header, cái nào cho JSON.
        //
        // TODO 7 (mục 9): Thử format Instant.now() bằng ofPattern("yyyy-MM-dd").
        //   Dán lại exception. Rồi thêm .withZone(ZoneId.systemDefault()) và chạy lại.
        //
        // TODO 8 (mục 9): Parse "2026-02-31" bằng ResolverStyle mặc định và bằng STRICT
        //   (nhớ đổi yyyy thành uuuu cho STRICT). In hai kết quả và kết luận.
        //
        // TODO 9 (mục 8): Dùng ofLocalizedDateTime(FormatStyle.MEDIUM) in cùng một mốc theo
        //   Locale.US, Locale.of("vi"), Locale.JAPAN. So sánh với việc tự viết pattern.
        //
        // TODO 10 (mục 10): Viết hàm parseAnToan(String) trả về Optional<LocalDate>, bắt
        //   DateTimeParseException và in getParsedString() + getErrorIndex() trước khi
        //   trả Optional.empty(). Test với ba chuỗi hỏng khác nhau.
        //
        // TODO 11 (mục 6): Viết thử một benchmark thô: tạo mới DateTimeFormatter trong vòng
        //   lặp 100_000 lần, so với dùng lại hằng static. Đo bằng System.nanoTime().
        //
        // TODO 12 (mục 10): Nếu có dự án Spring, viết một endpoint nhận
        //   @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate ngay và thử gửi
        //   chuỗi sai. Ghi lại mã HTTP trả về.
    }
}
