package com.javacore.api.date_time_regex.date_time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Legacy Date-Time Code
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 13/13 - bài CUỐI của section The Date Time API.
 * - Bài này có vẻ "chỉ để tham khảo" nhưng thực tế dùng rất nhiều: mọi dự án Java có tuổi
 * đều còn {@code java.util.Date} ở đâu đó, và mọi đợt nâng cấp đều phải đi qua bài này.
 * - Một câu cho cả bài: có sẵn SÁU method cầu nối giữa hai thế giới - dùng chúng để chuyển
 * dần từng chỗ, không cần viết lại toàn bộ một lúc.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/legacy-code/">dev.java - Legacy Date-Time Code</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - LEGACY DATE-TIME CODE (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. API CŨ HỎNG Ở ĐÂU
 * - Trước Java SE 8, cơ chế ngày giờ dựa trên {@code java.util.Date},
 * {@code java.util.Calendar}, {@code java.util.TimeZone} và {@code java.util.GregorianCalendar}.
 * - Ba nhược điểm trang gốc nêu, nguyên văn:
 * + "The Calendar class was not type safe"
 * + "Because the classes were mutable, they could not be used in multithreaded applications"
 * + "Bugs in application code were common due to the unusual numbering of months and the
 * lack of type safety"
 * <p>
 * 2. SÁU METHOD CẦU NỐI - PHẦN PHẢI THUỘC CỦA BÀI
 * - (Interoperability with Legacy Code) JDK 8 bổ sung các method chuyển đổi hai chiều:
 * + {@code Calendar.toInstant()}
 * + {@code GregorianCalendar.toZonedDateTime()}
 * + {@code GregorianCalendar.from(ZonedDateTime)}
 * + {@code Date.from(Instant)}
 * + {@code Date.toInstant()}
 * + {@code TimeZone.toZoneId()}
 * - Lưu ý nguyên văn: "Note that a time zone must be supplied to convert from an
 * {@code Instant} to a {@code ZonedDateTime}" - lại là chuyện đã gặp ở bài t7 mục 4.
 * <p>
 * 3. BẢN ĐỒ LỚP CŨ SANG LỚP MỚI
 * - (Mapping Legacy Date and Time Functionality to the Date Time API)
 * - {@code Date} ↔ {@code Instant}. Trang gốc nói hai lớp này tương tự nhau vì mỗi lớp đều:
 * "Represents an instantaneous point of time on the timeline (UTC)", "Holds a time
 * independent of a time zone", "Is represented as epoch-seconds (since 1970-01-01T00:00:00Z)
 * plus nanoseconds". Chuyển bằng {@code Date.from(Instant)} và {@code Date.toInstant()}.
 * - {@code GregorianCalendar} → {@code ZonedDateTime}: "The {@code ZonedDateTime} class is
 * the replacement for {@code GregorianCalendar}." Nó tách thành bốn phần rõ ràng:
 * {@code LocalDate} (năm/tháng/ngày), {@code LocalTime} (giờ/phút/giây/nano),
 * {@code ZoneId} (múi giờ), {@code ZoneOffset} (độ lệch hiện tại so với GMT).
 * - {@code TimeZone} → {@code ZoneId} hoặc {@code ZoneOffset}: "The {@code ZoneId} class
 * specifies a time zone identifier and has access to the rules used each time zone. The
 * {@code ZoneOffset} class specifies only an offset from Greenwich/UTC."
 * - {@code GregorianCalendar} đặt ngày về 1970-01-01 chỉ để lấy phần giờ → thay bằng
 * {@code LocalTime}.
 * - {@code GregorianCalendar} đặt giờ về 00:00 chỉ để lấy phần ngày → thay bằng
 * {@code LocalDate}. Trang gốc nói thẳng cách cũ là SAI: "(This {@code GregorianCalendar}
 * approach was flawed, as midnight does not occur in some countries once a year due to the
 * transition to daylight saving time.)" Đây đúng là cái bẫy đã ghi ở bài t5 mục 5.
 * <p>
 * 4. ĐỊNH DẠNG
 * - (Date and Time Formatting)
 * - "Although the {@code java.time.format.DateTimeFormatter} provides a powerful mechanism
 * for formatting date and time values, you can also use the {@code java.time} temporal-based
 * classes directly with {@code java.util.Formatter} and {@code String.format()}, using the
 * same pattern-based formatting that you use with the {@code java.util} date and time classes."
 * <p>
 * 5. KẾT LUẬN CỦA TRANG GỐC
 * - "If you want to use the rich functionality offered by the {@code java.time} package, your
 * easiest solution is to use the {@code toInstant()} or {@code toZonedDateTime()} methods
 * listed in the previous section. However, if you do not want to use that approach or it is
 * not sufficient for your needs, then you must rewrite your date-time code."
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. BẢNG CHUYỂN ĐỔI DÙNG ĐƯỢC NGAY
 * <p>
 * .  Cũ                                Mới                  Cách chuyển
 * .  java.util.Date                    Instant              date.toInstant()
 * .  java.util.Date                    LocalDateTime        date.toInstant().atZone(zone).toLocalDateTime()
 * .  java.sql.Date                     LocalDate            sqlDate.toLocalDate()
 * .  java.sql.Time                     LocalTime            sqlTime.toLocalTime()
 * .  java.sql.Timestamp                Instant              ts.toInstant()
 * .  Calendar                          Instant              cal.toInstant()
 * .  GregorianCalendar                 ZonedDateTime        gc.toZonedDateTime()
 * .  TimeZone                          ZoneId               tz.toZoneId()
 * .  SimpleDateFormat                  DateTimeFormatter    viết lại pattern, xem bài t8
 * <p>
 * - BẪY {@code java.sql.Date}: nó KẾ THỪA {@code java.util.Date} nhưng gọi
 * {@code toInstant()} trên nó thì ném {@code UnsupportedOperationException}. Phải dùng
 * {@code toLocalDate()}. Đây là vi phạm nguyên tắc Liskov nằm sẵn trong JDK, và là lỗi
 * runtime rất hay gặp khi refactor.
 * - {@code java.sql.Timestamp} cũng kế thừa {@code java.util.Date} nhưng {@code equals()}
 * của nó KHÔNG đối xứng với lớp cha. Đừng bao giờ so sánh hai lớp này với nhau.
 * <p>
 * 7. BA BẪY KHI ĐỌC CODE CŨ
 * - THÁNG 0-BASED: {@code new GregorianCalendar(2026, 0, 19)} là 19/01/2026.
 * Copy số tháng sang {@code LocalDate.of()} mà quên {@code +1} là lệch một tháng, IM LẶNG.
 * Đây là lỗi refactor số một. ({@code Calendar.JANUARY == 0}, xem lại bài t3 mục 4.)
 * - NĂM LỆCH 1900: {@code new Date(126, 9, 19)} là năm 2026 vì constructor đó nhận
 * "năm - 1900". Constructor này đã deprecated từ lâu nhưng vẫn còn trong code cũ.
 * - {@code Date} THỰC CHẤT CÓ CẢ GIỜ: tên lớp là "Date" nhưng nó lưu cả giờ tới milli.
 * Rất nhiều code cũ dùng nó làm "ngày" rồi so sánh bằng {@code equals()} và luôn ra
 * {@code false} vì phần giờ khác nhau. Chuyển sang {@code LocalDate} là hết bệnh.
 * <p>
 * 8. CHIẾN LƯỢC REFACTOR THỰC TẾ - LÀM THEO THỨ TỰ NÀY
 * - BƯỚC 1: đổi từ TRONG RA NGOÀI. Đổi kiểu trong entity và domain trước, giữ nguyên chữ ký
 * API công khai, dùng method cầu nối ở mục 2 tại ranh giới.
 * - BƯỚC 2: sửa tầng persistence. JPA 2.2+ hỗ trợ sẵn {@code java.time}, KHÔNG cần
 * {@code @Temporal} nữa - annotation đó chỉ dành cho {@code Date}/{@code Calendar}.
 * Để lại {@code @Temporal} trên field {@code LocalDate} là lỗi.
 * - BƯỚC 3: sửa tầng API. Đây là chỗ ĐỔI HỢP ĐỒNG nên phải cẩn thận nhất: Jackson serialize
 * {@code Date} thành số milli, còn {@code Instant} mặc định cũng ra số nhưng
 * {@code LocalDateTime} thì ra mảng - client sẽ vỡ. Bật
 * {@code write-dates-as-timestamps=false} và thông báo cho bên tiêu thụ API.
 * - BƯỚC 4: dọn {@code SimpleDateFormat}. Làm sau cùng vì nó thường ở tầng hiển thị,
 * ít rủi ro nhất.
 * - Xuyên suốt: có test trước khi đổi. Không có test thì viết test đặc tả hành vi hiện tại
 * trước đã - kể cả hành vi đang sai, để biết mình đổi cái gì.
 * <p>
 * 9. HAI THỨ KHÔNG BAO GIỜ DÙNG LẠI
 * - {@code SimpleDateFormat} làm field dùng chung: không thread-safe, xem bài t8 mục 6.
 * - {@code Date} làm kiểu trong API công khai hoặc domain model: nó mutable, nên ai cũng
 * sửa được đối tượng bạn trả về. Nếu buộc phải trả {@code Date} thì sao chép phòng vệ
 * ({@code new Date(d.getTime())}) mỗi lần vào và ra - đó là lý do nữa để bỏ nó.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - {@code java.util.Date} gần như là bản sao của {@code Date} trong JS: mutable, tháng
 * 0-based, mang sẵn múi giờ. Chúng cùng thời và cùng mắc một loạt lỗi thiết kế.
 * - Hệ JS đang đi đúng con đường Java đã đi 10 năm trước: {@code Date} cũ giữ lại vì tương
 * thích ngược, {@code Temporal} mới bất biến và tách kiểu rõ ràng.
 * - Nên khi chuyển {@code Date} → {@code Temporal} bên TS sau này, kinh nghiệm từ bài này
 * dùng lại được nguyên: đổi từ trong ra ngoài, cầu nối ở ranh giới, đổi hợp đồng API sau cùng.
 * - Bài học chung: API thời gian mutable là sai lầm thiết kế mà ngôn ngữ nào cũng phải trả giá.
 * <p>
 * ======================================================================
 * TỔNG KẾT CẢ SECTION
 * ======================================================================
 * <p>
 * 11. MƯỜI ĐIỀU MANG ĐI TỪ 13 BÀI
 * - (t1) Mọi lớp bất biến; {@code plus/minus/with} trả về bản mới, phải gán lại.
 * - (t2) Human time và machine time là hai thế giới; chọn sai lớp là sai từ gốc.
 * - (t3) Tháng và thứ dùng enum 1-based, khác hẳn {@code Calendar} cũ và JS.
 * - (t4) {@code plusMonths} cắt ngày về cuối tháng và KHÔNG đảo ngược được.
 * - (t5) "Local" nghĩa là KHÔNG có múi giờ, không phải "giờ máy mình".
 * - (t6) Offset là con số, zone là bộ luật; gap và overlap DST là nguồn bug thật.
 * - (t7) {@code Instant} là UTC, không hiểu đơn vị lịch, là kiểu đúng cho timestamp.
 * - (t8) {@code DateTimeFormatter} là hằng {@code static final}; cẩn thận {@code YYYY},
 * {@code mm}, {@code DD}.
 * - (t9/t10) {@code TemporalAdjusters} gói quy tắc nghiệp vụ; {@code Period} đếm lịch,
 * {@code Duration} đếm giây.
 * - (t11) Tiêm {@code Clock} thay vì gọi {@code now()} - đổi lại là test được.
 * <p>
 * 12. CÒN THIẾU GÌ SO VỚI PRODUCTION (ngoài phạm vi dev.java)
 * - Map {@code java.time} trong JPA và kiểu cột của Postgres/Oracle (đã rải ở t2, t6, t7).
 * - Cấu hình Jackson cho {@code java.time} trong REST API.
 * - Lịch âm Việt Nam (t12 mục 5).
 * - Nếu muốn, mở thêm một file {@code t14_SpringJpaJacksonIntegration} ngoài lộ trình để
 * gom ba thứ trên lại - nhưng làm sau khi xong 13 bài.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 13. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba nhược điểm của API cũ mà trang gốc nêu là gì?
 * - Kể sáu method cầu nối JDK 8 thêm vào.
 * - {@code Date} tương ứng lớp mới nào? {@code GregorianCalendar} thì tương ứng lớp nào?
 * - Chuyển {@code Instant} sang {@code ZonedDateTime} cần thêm gì?
 * - Vì sao cách cũ "đặt giờ về 00:00 để lấy phần ngày" là sai?
 * - {@code java.sql.Date.toInstant()} cho ra gì? Phải dùng method nào thay thế?
 * - Trong {@code new GregorianCalendar(2026, 0, 19)} thì số 0 là tháng mấy?
 * - {@code @Temporal} dùng cho kiểu nào, có được để trên field {@code LocalDate} không?
 * - Bốn bước refactor theo thứ tự nào, vì sao tầng API làm sau cùng?
 * - Hai thứ nào không bao giờ nên dùng lại, mỗi thứ một lý do?
 */
public class t13_LegacyDateTimeCode {

    void main() {
        // TODO 1 (mục 2): Tạo một java.util.Date, chuyển sang Instant rồi sang ZonedDateTime
        //   theo Asia/Ho_Chi_Minh, rồi chuyển ngược về Date. Xác nhận bằng nhau.
        // ========================================================
        // 1. TỪ CŨ SANG MỚI: java.util.Date -> Instant / LocalDateTime
        // ========================================================
        Date ngayCu = new Date(); // Lấy thời điểm hiện tại kiểu cũ
        System.out.println("1. Kiểu cũ (Date)        : " + ngayCu);

        // Bước 1: Dùng toInstant() làm cầu nối trung gian
        Instant instant = ngayCu.toInstant();
        System.out.println("2. Sang Instant (UTC)    : " + instant);

        // Bước 2: Chuyển sang LocalDateTime theo múi giờ máy
        LocalDateTime ngayMoi = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println("3. Sang LocalDateTime    : " + ngayMoi);

        // ========================================================
        // 2. TỪ MỚI VỀ CŨ: Instant / LocalDateTime -> java.util.Date
        // ========================================================
        // Dùng Date.from(instant)
        Date quayLaiKieuCu = Date.from(ngayMoi.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("4. Quay về kiểu cũ (Date): " + quayLaiKieuCu);
        // TODO 2 (mục 2): Làm tương tự với GregorianCalendar <-> ZonedDateTime và
        //   TimeZone <-> ZoneId. Mỗi cặp in cả hai chiều.
        //
        // TODO 3 (mục 7 - BẪY THÁNG): Tạo new GregorianCalendar(2026, 0, 19) và in
        //   getTime(). Rồi tạo LocalDate.of(2026, 0+1, 19). Viết comment giải thích vì sao
        //   copy số 0 sang LocalDate là sai.
        //
        // TODO 4 (mục 7): Thử new Date(126, 9, 19) (bỏ qua cảnh báo deprecated) và in ra.
        //   Xác nhận nó là năm 2026. Ghi lại công thức năm - 1900.
        //
        // TODO 5 (mục 7): Tạo hai java.util.Date cùng NGÀY nhưng khác giờ. So equals().
        //   Rồi chuyển cả hai sang LocalDate và so lại. Đây là bệnh của code cũ.
        //
        // TODO 6 (mục 6 - BẪY java.sql.Date): Tạo java.sql.Date, gán nó vào một biến kiểu
        //   java.util.Date, rồi gọi toInstant(). Dán lại exception. Giải thích vì sao đây là
        //   vi phạm Liskov.
        //
        // TODO 7 (mục 3): Viết lại đoạn code cũ sau bằng java.time:
        //     Calendar c = Calendar.getInstance();
        //     c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
        //     c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        //     Date dauNgay = c.getTime();
        //   Giải thích vì sao bản cũ sai ở nước có DST (gợi ý: bài t5 mục 5).
        //
        // TODO 8 (mục 1): Chứng minh Calendar là mutable: truyền một Calendar vào một method,
        //   method đó gọi add() lên nó, rồi in giá trị ở ngoài. So với việc làm điều tương tự
        //   với LocalDate.
        //
        // TODO 9 (mục 4): Format cùng một LocalDateTime bằng String.format("%tF %tT", ...)
        //   và bằng DateTimeFormatter. So hai cách và nhận xét cách nào rõ hơn.
        //
        // TODO 10 (mục 8): Grep dự án tìm java.util.Date, Calendar, SimpleDateFormat.
        //   Lập danh sách theo tầng (entity / service / controller / util) và đề xuất
        //   thứ tự xử lý theo bốn bước ở mục 8. Chưa cần sửa.
        //
        // TODO 11 (mục 11): Tự trả lời 10 ý tổng kết ở mục 11 mà KHÔNG mở lại các file t1-t12.
        //   Mở ra đối chiếu sau. Ý nào trả lời không được thì quay lại đọc bài đó.
    }
}
