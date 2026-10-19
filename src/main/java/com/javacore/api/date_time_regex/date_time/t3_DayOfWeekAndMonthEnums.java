package com.javacore.api.date_time_regex.date_time;

import java.time.DayOfWeek;

/**
 * DayOfWeek and Month Enums
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 3/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Bài ngắn, dễ. Nhưng nó dựng sẵn hai thứ dùng lại suốt phần còn lại của section:
 * enum {@code DayOfWeek}, enum {@code Month}, và cặp {@code TextStyle} + {@code Locale}.
 * - Một câu cho cả bài: hai enum này thay số nguyên trần bằng kiểu có tên, và bản thân chúng
 * cũng biết làm số học thời gian chứ không chỉ là hằng số.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/dayofweek-month/">dev.java - DayOfWeek and Month Enums</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - DAYOFWEEK AND MONTH ENUMS (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. ENUM DAYOFWEEK
 * - Bảy hằng: {@code MONDAY} tới {@code SUNDAY}.
 * - Giá trị số từ 1 (Monday) tới 7 (Sunday), lấy bằng {@code getValue()}.
 * - LÀM ĐƯỢC SỐ HỌC: cộng/trừ ngày ngay trên enum, ví dụ cộng 3 ngày vào {@code MONDAY}
 * ra {@code THURSDAY}. Method là {@code plus(long)} và {@code minus(long)}, tự quay vòng.
 * - Lấy tên hiển thị bằng {@code getDisplayName(TextStyle, Locale)}.
 * - {@code TextStyle} có các kiểu: {@code FULL}, {@code NARROW}, {@code SHORT} và các biến
 * thể {@code STANDALONE} tương ứng.
 * - Trang gốc nhấn: dùng hằng có tên như {@code DayOfWeek.FRIDAY} làm code rõ hơn hẳn số 5.
 * <p>
 * 2. ENUM MONTH
 * - Mười hai hằng: {@code JANUARY} tới {@code DECEMBER}, giá trị số 1-12 theo chuẩn ISO.
 * - {@code maxLength()} trả về số ngày TỐI ĐA của tháng - tháng Hai trả về 29.
 * Cặp đôi của nó là {@code minLength()} (tháng Hai trả về 28).
 * - {@code getDisplayName(TextStyle, Locale)} như trên.
 * - Lại nhấn cùng một ý: {@code Month.SEPTEMBER} dễ đọc hơn số 9.
 * <p>
 * 3. ĐIỂM CHUNG CỦA HAI ENUM
 * - Cả hai đều hỗ trợ hiển thị theo locale, và đều có method tính toán thời gian.
 * - Cả hai đều là thành phần chuẩn của Date-Time API chứ không phải tiện ích phụ.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. ĐÁNH SỐ 1-BASED - KHÁC HẲN JAVA CŨ VÀ KHÁC HẲN JAVASCRIPT
 * - {@code Month.JANUARY.getValue()} trả về 1. {@code LocalDate.of(2026, 1, 19)} là tháng Một.
 * - {@code java.util.Calendar} cũ dùng 0-based ({@code Calendar.JANUARY == 0}) và JS cũng
 * 0-based ({@code new Date(2026, 0, 19)} là tháng Một). Đây là nguồn bug kinh điển mà
 * {@code java.time} cố tình sửa.
 * - Bài t13 sẽ thấy lại chuyện này khi convert code cũ: copy số tháng từ {@code Calendar}
 * sang {@code LocalDate} mà quên +1 là lệch một tháng, im lặng, không lỗi.
 * <p>
 * 5. VALUES() VÀ ORDINAL() - BẪY CẦN TRÁNH
 * - Enum nào cũng có {@code ordinal()} (0-based, theo thứ tự khai báo) và {@code getValue()}
 * (1-based, theo ngữ nghĩa lịch). CHỈ dùng {@code getValue()}.
 * - {@code ordinal()} là chi tiết cài đặt của ngôn ngữ, không phải ngữ nghĩa nghiệp vụ. Dùng
 * nó cho số tháng là tự tạo bug lệch 1.
 * - Chiều ngược lại dùng {@code Month.of(int)} và {@code DayOfWeek.of(int)}, cũng 1-based.
 * Truyền 0 hay 13 thì ném {@code DateTimeException} chứ không trả về rác - đây là điểm hơn
 * hẳn mảng hằng số tự chế.
 * <p>
 * 6. TEXTSTYLE - BẢNG ĐỐI CHIẾU THỰC TẾ
 * - Với {@code Month.JANUARY}, locale {@code Locale.ENGLISH}:
 * + {@code FULL} → "January"
 * + {@code SHORT} → "Jan"
 * + {@code NARROW} → "J"
 * - Các biến thể {@code FULL_STANDALONE}, {@code SHORT_STANDALONE}, {@code NARROW_STANDALONE}
 * dành cho ngôn ngữ có BIẾN CÁCH: tên tháng khi đứng một mình khác với tên tháng khi nằm
 * trong một ngày cụ thể. Tiếng Nga, Séc, Phần Lan có hiện tượng này; tiếng Anh và tiếng Việt
 * thì hai dạng giống nhau nên không thấy khác biệt.
 * - {@code NARROW} KHÔNG đảm bảo phân biệt được: tháng Một, Sáu, Bảy trong tiếng Anh đều ra
 * "J". Đừng bao giờ dùng {@code NARROW} cho dữ liệu cần parse ngược lại, chỉ dùng để hiển thị
 * trên giao diện hẹp.
 * <p>
 * 7. LOCALE - ĐỪNG DÙNG BẢN KHÔNG THAM SỐ
 * - Có overload {@code getDisplayName(TextStyle)} dùng locale mặc định của JVM. TRÁNH nó
 * trong code production: locale mặc định phụ thuộc máy chạy, nên code chạy đúng trên laptop
 * dev lại ra tiếng khác trên server.
 * - Luôn truyền {@code Locale} tường minh. Trong Spring MVC thì lấy từ
 * {@code LocaleContextHolder.getLocale()} để tôn trọng header {@code Accept-Language} của
 * request, không phải locale của server.
 * - Dữ liệu tên tháng/tên thứ đến từ CLDR (bài t1 mục 1), nên chất lượng bản dịch là của
 * Unicode chứ không phải tự JDK bịa ra.
 * <p>
 * 8. HAI ENUM NÀY LÀ TEMPORALACCESSOR VÀ TEMPORALADJUSTER
 * - Chúng không chỉ là hằng số: {@code Month} cài {@code TemporalAccessor} và
 * {@code TemporalAdjuster}, nên viết được {@code date.with(Month.MARCH)} để đổi tháng.
 * - Tương tự {@code date.with(DayOfWeek.FRIDAY)}: trả về thứ Sáu TRONG CÙNG TUẦN ISO của
 * ngày đó - có thể lùi về quá khứ nếu ngày gốc là thứ Bảy. Đây là chỗ dễ hiểu nhầm, bài t9
 * sẽ phân biệt với {@code TemporalAdjusters.next(DayOfWeek.FRIDAY)}.
 * - Hiểu được câu này cần bài t9; ghi lại đây để lúc học t9 nối được mạch.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - {@code DayOfWeek}/{@code Month} không có tương đương trong JS: {@code getMonth()} trả về
 * {@code number} 0-11, {@code getDay()} trả về {@code number} 0-6 với 0 là Chủ nhật. Hai hệ
 * đánh số khác nhau ngay trong cùng một API.
 * - Java: {@code DayOfWeek} bắt đầu từ Thứ Hai = 1 (chuẩn ISO), {@code Month} từ 1.
 * Nhất quán, và quan trọng hơn là có KIỂU nên không truyền nhầm tháng vào chỗ cần thứ.
 * - Bên TS muốn tương đương phải tự viết {@code type Month = 1|2|...|12} hoặc dùng enum,
 * rồi vẫn phải tự viết hàm cộng trừ quay vòng. Java cho sẵn {@code plus()}/{@code minus()}.
 * - {@code getDisplayName(TextStyle, Locale)} ≈ {@code Intl.DateTimeFormat(locale, {month: 'long'})}.
 * Cả hai đều lấy dữ liệu từ CLDR, nên kết quả khớp nhau.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code DayOfWeek.MONDAY.getValue()} trả về mấy? {@code SUNDAY} thì mấy?
 * - {@code Month.JANUARY.getValue()} trả về mấy? {@code Calendar.JANUARY} cũ trả về mấy?
 * - Vì sao không được dùng {@code ordinal()} thay cho {@code getValue()}?
 * - {@code Month.FEBRUARY.maxLength()} và {@code minLength()} trả về gì, vì sao khác nhau?
 * - Ba giá trị {@code TextStyle} cơ bản, mỗi cái ra chuỗi thế nào với tháng Một tiếng Anh?
 * - Biến thể {@code STANDALONE} sinh ra để giải quyết chuyện gì?
 * - Vì sao không nên dùng {@code getDisplayName(TextStyle)} bản không truyền {@code Locale}?
 * - Trong Spring MVC thì lấy {@code Locale} đúng từ đâu?
 * - {@code DayOfWeek.SATURDAY.plus(3)} ra gì? Nó có ném exception khi vượt quá Chủ nhật không?
 */
public class t3_DayOfWeekAndMonthEnums {

    void main() {
        // TODO 1 (mục 1, 4): In tất cả giá trị DayOfWeek.values() kèm getValue() và ordinal()
        //   trên cùng một dòng. Quan sát hai cột lệch nhau đúng 1 và ghi lại kết luận.
        //
        DayOfWeek thu = DayOfWeek.FRIDAY;

        System.out.println("thu          : " + thu);
        System.out.println("so thu tu    : " + thu.getValue());
        System.out.println("cong 3 ngay  : " + thu.plus(3));
        System.out.println("cong 10 ngay : " + thu.plus(10));
        // TODO 2 (mục 2, 4): Làm tương tự với Month.values(), in kèm minLength() và maxLength().
        //   Chỉ ra đúng một tháng có hai giá trị khác nhau.
        //
        // TODO 3 (mục 6, 7): In tên tháng hiện tại theo ba TextStyle (FULL, SHORT, NARROW)
        //   và ba Locale (ENGLISH, Locale.of("vi"), Locale.FRENCH). Bảng 3x3, có nhãn.
        //
        // TODO 4 (mục 6): In NARROW của cả 12 tháng bằng Locale.ENGLISH. Đếm xem có bao nhiêu
        //   chữ "J" và kết luận vì sao không dùng NARROW để parse.
        //
        // TODO 5 (mục 1): Viết vòng lặp cộng dần 1 tới 10 ngày vào DayOfWeek.FRIDAY bằng plus().
        //   Xác nhận nó quay vòng chứ không ném exception. So với việc tự tính (5 + n) % 7.
        //
        // TODO 6 (mục 5): Gọi Month.of(0) và Month.of(13). Dán lại exception.
        //   Rồi thử Month.values()[0] và giải thích vì sao cách này nguy hiểm hơn.
        //
        // TODO 7 (mục 2): Viết hàm nhận YearMonth, trả về số ngày thật của tháng đó.
        //   So sánh kết quả với Month.maxLength() cho tháng Hai năm nhuận và năm thường.
        //   Kết luận: vì sao maxLength() một mình KHÔNG đủ dùng.
        //
        // TODO 8 (mục 8): Lấy LocalDate hôm nay, thử date.with(DayOfWeek.MONDAY) và
        //   date.with(DayOfWeek.SUNDAY). Chạy vào các ngày khác nhau trong tuần và ghi lại
        //   khi nào kết quả LÙI về quá khứ. Đây là chuẩn bị cho bài t9.
        //
        // TODO 9 (mục 7): In Locale.getDefault() của máy mình. Thử đặt
        //   -Duser.language=fr khi chạy và in lại tên tháng bằng overload KHÔNG truyền Locale.
        //   Đây là mô phỏng chuyện xảy ra khi deploy lên server khác locale.
    }
}
