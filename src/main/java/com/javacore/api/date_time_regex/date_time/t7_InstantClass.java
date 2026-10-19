package com.javacore.api.date_time_regex.date_time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Instant
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 7/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Lớp đại diện cho THỜI GIAN MÁY (khái niệm ở bài t2 mục 1). Đối trọng của
 * {@code LocalDateTime}.
 * - Một câu cho cả bài: {@code Instant} là một con số trên dòng thời gian, không biết gì về
 * năm tháng ngày - và chính vì không biết nên nó mới là thứ duy nhất so sánh được giữa các
 * máy, các múi giờ.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/instant/">dev.java - Instant</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - INSTANT (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. INSTANT LÀ GÌ
 * - (The Instant Class)
 * - Biểu diễn "the start of a nanosecond on the timeline". Không phải "một khoảnh khắc mơ hồ"
 * mà là mốc bắt đầu của một nanosecond cụ thể.
 * - Là thành phần lõi để sinh dấu thời gian kiểu máy.
 * <p>
 * 2. EPOCH VÀ CÁC HẰNG
 * - Mốc gốc: 01/01/1970 lúc 00:00:00Z, gọi là EPOCH.
 * - Trước mốc đó là giá trị ÂM, sau là dương.
 * - Hằng {@code MIN} và {@code MAX} là giới hạn nhỏ nhất/lớn nhất biểu diễn được.
 * - {@code toString()} cho ra chuỗi theo định dạng ISO-8601, kết thúc bằng {@code Z}.
 * <p>
 * 3. CÁC METHOD THAO TÁC
 * - {@code plus()} / {@code minus()} để dịch thời điểm.
 * - {@code isAfter()} / {@code isBefore()} để so sánh.
 * - {@code until()} để tính khoảng giữa hai {@code Instant}.
 * <p>
 * 4. GIỚI HẠN VÀ CHUYỂN ĐỔI
 * - Giới hạn quan trọng nhất, nguyên văn: {@code Instant} "does not work with human units of
 * time, such as years, months, or days".
 * - Muốn tính theo đơn vị người thì phải GẮN MÚI GIỜ để chuyển sang {@code LocalDateTime}
 * hoặc {@code ZonedDateTime}, qua {@code ofInstant()}.
 * - Chiều đi thì dễ: {@code ZonedDateTime} và {@code OffsetDateTime} đều chuyển về
 * {@code Instant} được. Chiều về thì BẮT BUỘC phải cấp thông tin múi giờ - vì một
 * {@code Instant} ứng với nhiều giờ địa phương khác nhau tùy nơi.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. BÊN TRONG INSTANT CÓ GÌ
 * - Hai trường: {@code long} số giây từ epoch, và {@code int} nanosecond trong giây đó.
 * Không có gì khác. Không có múi giờ, không có lịch.
 * - Vì vậy {@code equals()} và {@code compareTo()} của nó chỉ là so hai con số - luôn đúng,
 * không phụ thuộc máy chạy ở đâu. Đây là lý do mọi log, mọi audit trail nên dùng
 * {@code Instant}.
 * - {@code Instant} luôn ở UTC. Nói "Instant theo giờ Việt Nam" là câu vô nghĩa.
 * <p>
 * 6. CHỈ HỖ TRỢ ĐƠN VỊ TỚI DAYS - VÀ "DAYS" Ở ĐÂY LÀ 24 GIỜ CỨNG
 * - {@code instant.plus(1, ChronoUnit.MONTHS)} ném {@code UnsupportedTemporalTypeException}.
 * Trang gốc đã báo trước; bài t9 mục về {@code ChronoUnit} nói rõ hơn.
 * - {@code plus(1, ChronoUnit.DAYS)} thì CHẠY, nhưng nó cộng đúng 86400 giây - không phải
 * "ngày lịch". Vào ngày đổi DST, kết quả lệch một tiếng so với trực giác.
 * - Luật: cần "cùng giờ này ngày mai theo lịch" thì phải qua {@code ZonedDateTime} rồi mới
 * cộng. Cần "đúng 24 tiếng sau" thì {@code Instant} là đúng.
 * - Đây chính là phân biệt {@code Duration} với {@code Period} sẽ học ở bài t10.
 * <p>
 * 7. BỘ METHOD EPOCH - DÙNG KHI GIAO TIẾP VỚI HỆ THỐNG KHÁC
 * - {@code Instant.ofEpochMilli(long)} / {@code toEpochMilli()} - đây là cầu nối với JS,
 * với Redis, với các API trả về số milli.
 * - {@code Instant.ofEpochSecond(long)} / {@code getEpochSecond()} - cầu nối với Unix
 * timestamp, với JWT ({@code exp}, {@code iat} đều là epoch SECOND, không phải milli -
 * nhầm chỗ này là token hết hạn sai 1000 lần).
 * - CẢNH BÁO MẤT ĐỘ CHÍNH XÁC: {@code toEpochMilli()} cắt mất nanosecond. Đi vòng
 * {@code Instant → milli → Instant} KHÔNG trả lại giá trị cũ. Nếu cần so sánh sau khi đi
 * vòng, nhớ {@code truncatedTo(ChronoUnit.MILLIS)} trước.
 * - {@code Instant.MAX.toEpochMilli()} ném {@code ArithmeticException} vì tràn {@code long}.
 * <p>
 * 8. SO SÁNH ĐỘ CHÍNH XÁC THỰC TẾ
 * - {@code Instant.now()} trên Java 8 chỉ chính xác tới MILLI. Từ Java 9 nó dùng đồng hồ
 * chính xác hơn, thường tới micro hoặc nano tùy hệ điều hành.
 * - Hệ quả thật: code chạy đúng trên Java 8 có thể hỏng test trên Java 17+ vì
 * {@code equals()} giữa giá trị trước và sau khi lưu database không còn khớp.
 * - {@code Instant.now()} KHÔNG đảm bảo tăng đơn điệu: nó lấy đồng hồ tường, mà đồng hồ
 * tường có thể bị NTP chỉnh lùi. Đo KHOẢNG THỜI GIAN thì dùng {@code System.nanoTime()},
 * đừng dùng {@code Instant}.
 * - Nhớ: {@code Instant} trả lời "lúc nào", {@code System.nanoTime()} trả lời "mất bao lâu".
 * <p>
 * 9. INSTANT Ở TẦNG SPRING VÀ JPA
 * - Kiểu nên dùng cho {@code createdAt}/{@code updatedAt} với {@code @CreationTimestamp} /
 * {@code @UpdateTimestamp} của Hibernate, hoặc {@code @CreatedDate} của Spring Data Auditing.
 * - Map xuống Postgres {@code timestamptz}. Nhớ đặt
 * {@code spring.jpa.properties.hibernate.jdbc.time_zone=UTC} như bài t6 mục 11.
 * - Jackson serialize {@code Instant} thành SỐ epoch theo mặc định, không phải chuỗi ISO.
 * Đổi bằng {@code spring.jackson.serialization.write-dates-as-timestamps=false}. Đây là
 * khác biệt hay làm client bất ngờ khi đổi kiểu từ {@code Date} sang {@code Instant}.
 * - Trong test, {@code Instant.now()} rải rác là thứ làm test không xác định. Bài t11
 * ({@code Clock}) giải quyết triệt để chuyện này.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Instant} ≈ {@code Date.now()} của JS (số milli từ epoch) - nhưng JS trả về
 * {@code number} trần, không có kiểu. Cộng nhầm một {@code number} khác vào không ai cản.
 * - Khác biệt độ chính xác: {@code Date.now()} chỉ tới milli; {@code Instant} tới nano.
 * Khi serialize {@code Instant} gửi cho client JS nhớ chuyện này.
 * - {@code Instant} ≈ {@code Temporal.Instant} của TC39, và {@code Temporal} cũng tách rõ
 * {@code Instant} với {@code PlainDateTime} giống hệt Java.
 * - {@code System.nanoTime()} ≈ {@code performance.now()} của JS - cũng cùng một lời khuyên:
 * đo khoảng thời gian thì dùng cái này, đừng dùng đồng hồ tường.
 * - Bên JS, {@code new Date(epochMilli)} rồi {@code getFullYear()} luôn trả về theo giờ
 * trình duyệt. Java bắt bạn viết rõ {@code instant.atZone(zone).getYear()} - dài hơn nhưng
 * không giấu giả định nào.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code Instant} lưu đúng mấy trường, là những trường nào?
 * - EPOCH là mốc nào? Giá trị trước mốc đó biểu diễn thế nào?
 * - Vì sao câu "Instant theo giờ Việt Nam" là vô nghĩa?
 * - {@code instant.plus(1, ChronoUnit.MONTHS)} cho ra gì? Còn {@code DAYS} thì sao?
 * - {@code plus(1, DAYS)} trên {@code Instant} khác gì cộng 1 ngày trên {@code ZonedDateTime}?
 * - Chuyển {@code Instant} sang {@code LocalDateTime} cần thêm gì? Chiều ngược lại thì sao?
 * - JWT dùng epoch giây hay epoch milli? Nhầm thì hậu quả ra sao?
 * - {@code toEpochMilli()} có mất mát gì không?
 * - Đo thời gian chạy của một đoạn code thì dùng gì, vì sao KHÔNG dùng {@code Instant}?
 * - Jackson mặc định serialize {@code Instant} ra dạng gì? Đổi bằng property nào?
 */
public class t7_InstantClass {

    void main() {
        // TODO 1 (mục 1, 2): In Instant.now(), Instant.EPOCH, Instant.MIN, Instant.MAX.
        //   Quan sát chữ Z cuối chuỗi và đối chiếu với dấu hiệu nhận dạng ở bài t2 mục 3.
        //
        Instant moc = Instant.now();

        System.out.println("moc           : " + moc);
        System.out.println("+1 ngay (24h) : " + moc.plus(1, ChronoUnit.DAYS));
        System.out.println(moc.plus(1, ChronoUnit.MONTHS));          // <-- no o day
        System.out.println("doc o VN      : " + moc.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime());

        // TODO 2 (mục 5): In instant.getEpochSecond() và instant.getNano() riêng rẽ.
        //   Tự ghép lại bằng Instant.ofEpochSecond(sec, nano) và xác nhận equals().
        //
        // TODO 3 (mục 6): Gọi Instant.now().plus(1, ChronoUnit.MONTHS). Dán lại exception.
        //   Rồi gọi plus(1, ChronoUnit.DAYS) và xác nhận nó chạy. Giải thích vì sao DAYS
        //   được mà MONTHS thì không.
        //
        // TODO 4 (mục 6): Lấy ngày đổi DST của Europe/Berlin (tìm được ở bài t6 TODO 5).
        //   So sánh HAI cách cộng một ngày: instant.plus(1, DAYS) và
        //   instant.atZone(berlin).plusDays(1).toInstant(). In cả hai và chỉ ra chênh lệch.
        //
        // TODO 5 (mục 4): Lấy Instant.now(), chuyển sang LocalDateTime theo ba zone khác nhau.
        //   Xác nhận ba giờ khác nhau nhưng toInstant() ngược lại đều về cùng một giá trị.
        //
        // TODO 6 (mục 7): Viết vòng Instant -> toEpochMilli() -> ofEpochMilli() -> so equals()
        //   với bản gốc. Chạy vài lần. Rồi thêm truncatedTo(MILLIS) trước khi đi vòng và so lại.
        //
        // TODO 7 (mục 7): Giải mã phần payload của một JWT bất kỳ (tự tạo cũng được), lấy
        //   trường exp, dựng Instant từ nó. Thử nhầm ofEpochMilli và xem kết quả ra năm nào.
        //
        // TODO 8 (mục 3): Dùng until() tính số giây giữa Instant.EPOCH và bây giờ.
        //   Đối chiếu với getEpochSecond(). Rồi thử until() với ChronoUnit.YEARS và ghi lại
        //   chuyện gì xảy ra.
        //
        // TODO 9 (mục 8): Đo thời gian chạy một vòng lặp nặng bằng CẢ HAI cách: hiệu hai
        //   Instant.now(), và hiệu hai System.nanoTime(). Chạy 10 lần, so độ ổn định.
        //
        // TODO 10 (mục 8): In Instant.now() 5 lần liên tiếp không sleep. Đếm số chữ số phần
        //   thập phân - đó là độ phân giải đồng hồ của máy và JDK này.
    }
}
