package com.javacore.api.date_time_regex.date_time;

import java.time.*;

/**
 * Clock
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 11/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - TRANG GỐC RẤT NGẮN, không có ví dụ code nào. Nhưng đây là bài có giá trị production
 * CAO NHẤT của cả section - phần "ngoài trang gốc" bên dưới dài hơn hẳn phần trang gốc,
 * và đó là chủ ý.
 * - Một câu cho cả bài: {@code Clock} biến "bây giờ là mấy giờ" từ một LỜI GỌI TĨNH
 * không test được thành một PHỤ THUỘC tiêm vào được.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/clock/">dev.java - Clock</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CLOCK (mục 1 đến 2)
 * ======================================================================
 * <p>
 * 1. VÌ SAO CÓ LỚP CLOCK
 * - Phần lớn lớp temporal đều có method {@code now()} dùng đồng hồ hệ thống và múi giờ mặc định.
 * - Nhưng chúng cũng có overload nhận một {@code Clock}. Truyền {@code Clock} khác vào giúp
 * xử lý múi giờ tốt hơn VÀ phục vụ việc TEST.
 * <p>
 * 2. BA FACTORY METHOD TRANG GỐC NÊU
 * - {@code Clock} là lớp TRỪU TƯỢNG, không khởi tạo trực tiếp được.
 * - {@code Clock.offset(Clock, Duration)} - "returns a clock that is offset by the specified
 * {@code Duration}".
 * - {@code Clock.systemUTC()} - "returns a clock representing the Greenwich/UTC time zone".
 * - {@code Clock.fixed(Instant, ZoneId)} - "always returns the same {@code Instant}. For this
 * clock, time stands still".
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 3. VẤN ĐỀ THẬT MÀ CLOCK GIẢI
 * - Code có {@code LocalDate.now()} nằm sâu trong service là code KHÔNG TEST ĐƯỢC tử tế.
 * Muốn test "hóa đơn quá hạn 30 ngày" thì phải đổi đồng hồ máy, hoặc tạo dữ liệu tính
 * ngược từ {@code now()} - mà cách sau thì test tự lặp lại chính logic đang test, nên
 * không bắt được lỗi.
 * - Triệu chứng kinh điển: test pass cả năm rồi fail vào ngày 1 tháng 1, hoặc fail khi chạy
 * lúc 23:59, hoặc fail trên CI vì CI chạy UTC còn laptop chạy giờ Việt Nam.
 * - Giải pháp: KHÔNG gọi {@code now()} trực tiếp. Tiêm {@code Clock} vào, gọi
 * {@code LocalDate.now(clock)}. Test truyền {@code Clock.fixed(...)}, production truyền
 * {@code Clock.systemUTC()}.
 * - Đây đúng là Dependency Inversion áp vào một thứ tưởng như không thể: thời gian.
 * <p>
 * 4. CẤU HÌNH TRONG SPRING BOOT - BA DÒNG
 * - Khai một {@code @Bean} trong lớp config:
 * {@code @Bean Clock clock() { return Clock.systemUTC(); }}
 * - Service nhận {@code Clock} qua constructor như mọi dependency khác.
 * - Test dùng {@code @TestConfiguration} ghi đè bằng
 * {@code Clock.fixed(Instant.parse("2026-10-19T03:00:00Z"), ZoneOffset.UTC)}.
 * - Spring KHÔNG tự tạo bean {@code Clock} - phải tự khai. Đây là lý do nhiều dự án không
 * biết tới cách này.
 * - Quy ước đặt: dùng {@code systemUTC()} chứ đừng {@code systemDefaultZone()}, cho khớp
 * quy tắc vàng ở bài t6 mục 10.
 * <p>
 * 5. CÁC LOẠI CLOCK VÀ CÔNG DỤNG
 * - {@code Clock.systemUTC()} - production. Đồng hồ hệ thống, múi giờ UTC.
 * - {@code Clock.system(zoneId)} - đồng hồ hệ thống, múi giờ chỉ định.
 * - {@code Clock.systemDefaultZone()} - đồng hồ hệ thống, múi giờ của máy. Tránh ở tầng nghiệp vụ.
 * - {@code Clock.fixed(instant, zone)} - đứng im. Dùng cho test cần kết quả tất định.
 * - {@code Clock.offset(clock, duration)} - lệch một khoảng cố định. Dùng để test "ngày mai",
 * "30 ngày sau" mà vẫn để thời gian chạy.
 * - {@code Clock.tick(clock, duration)} - làm tròn xuống theo bước. {@code tickSeconds},
 * {@code tickMinutes} là hai bản tiện. Hữu ích khi cần độ chính xác thấp nhưng ổn định,
 * ví dụ tránh vấn đề nanosecond ở bài t7 mục 8.
 * - KHÔNG có {@code Clock} tua được sẵn trong JDK. Cần "tua thời gian" trong test thì tự viết
 * một lớp con giữ một {@code Instant} sửa được - khoảng 15 dòng, rất đáng viết.
 * <p>
 * 6. MỌI NOW() ĐỀU CÓ OVERLOAD NHẬN CLOCK
 * - {@code LocalDate.now(clock)}, {@code LocalDateTime.now(clock)}, {@code Instant.now(clock)},
 * {@code ZonedDateTime.now(clock)}, {@code LocalTime.now(clock)}, {@code Year.now(clock)}...
 * - {@code Clock} mang theo cả {@code ZoneId}, nên {@code LocalDate.now(clock)} lấy ngày theo
 * múi giờ của clock chứ không phải của máy. Đây chính là ý "better time zone handling" mà
 * trang gốc nhắc tới.
 * - Bản thân {@code Clock} cũng có {@code instant()}, {@code millis()}, {@code getZone()},
 * {@code withZone(zone)}.
 * <p>
 * 7. GIỚI HẠN CẦN BIẾT
 * - {@code Clock} KHÔNG chặn được việc ai đó vẫn viết {@code LocalDate.now()} không tham số.
 * Đây là kỷ luật của đội, không phải thứ compiler ép được. Cách chặn: thêm luật ArchUnit
 * hoặc rule của static analysis cấm gọi {@code now()} không tham số ngoài lớp config.
 * - Các annotation {@code @CreationTimestamp} của Hibernate và {@code @CreatedDate} của
 * Spring Data KHÔNG đi qua bean {@code Clock} của bạn. Spring Data có
 * {@code DateTimeProvider} để tùy biến; Hibernate thì khó hơn. Nếu cần test chính xác
 * {@code createdAt} thì tự gán trong service thay vì dựa vào annotation.
 * - {@code Clock.fixed()} làm thời gian đứng im hoàn toàn - test nào phụ thuộc "thời gian
 * trôi" (đo duration, timeout) sẽ hỏng. Chỗ đó dùng {@code Clock.offset()} hoặc clock tự viết.
 * <p>
 * 8. ĐỐI CHIẾU TYPESCRIPT
 * - Bên JS vấn đề y hệt: {@code Date.now()} rải khắp code thì test phải dùng
 * {@code jest.useFakeTimers()} hoặc thư viện như sinon để vá toàn cục.
 * - Vá toàn cục là cách giải TỒI hơn: nó sửa môi trường chứ không sửa thiết kế, dễ rò rỉ
 * giữa các test, và không nói lên được service nào thật sự phụ thuộc thời gian.
 * - {@code Clock} của Java là cách giải bằng THIẾT KẾ: phụ thuộc thời gian hiện rõ trong
 * constructor, nhìn chữ ký là biết.
 * - Mang ngược sang TS được: tiêm một {@code interface Clock { now(): Date }} thay vì gọi
 * {@code Date.now()} trực tiếp. Cùng một bài học, không phụ thuộc ngôn ngữ.
 * - {@code Clock.fixed()} ≈ {@code jest.setSystemTime(...)}, nhưng phạm vi hẹp hơn nhiều
 * nên an toàn hơn.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code Clock} có khởi tạo trực tiếp bằng {@code new} được không? Vì sao?
 * - Ba factory method mà trang gốc nêu, mỗi cái trả về đồng hồ thế nào?
 * - Hai lý do trang gốc đưa ra để dùng {@code Clock} thay cho {@code now()} là gì?
 * - Vì sao test "hóa đơn quá hạn 30 ngày" viết bằng {@code LocalDate.now()} là test yếu?
 * - Khai bean {@code Clock} trong Spring Boot thế nào? Spring có tự tạo sẵn không?
 * - Nên dùng {@code systemUTC()} hay {@code systemDefaultZone()} cho bean đó, vì sao?
 * - {@code LocalDate.now(clock)} lấy múi giờ từ đâu?
 * - Kể ba loại {@code Clock} ngoài ba cái trang gốc nêu.
 * - {@code Clock.fixed()} không hợp với loại test nào?
 * - {@code @CreationTimestamp} của Hibernate có đi qua bean {@code Clock} không?
 */
public class t11_Clock {

    void main() {
        // TODO 1 (mục 2): Tạo Clock.systemUTC(), Clock.systemDefaultZone(),
        //   Clock.system(ZoneId.of("Asia/Tokyo")). In instant() và getZone() của cả ba.
        //   Chỉ ra cái nào instant giống nhau, cái nào zone khác nhau.
        // 1. Clock thông thường (chạy theo giờ hệ thống hiện tại)
        Clock dongHoHeThong = Clock.systemDefaultZone();
        System.out.println("Hôm nay (thực tế)     : " + LocalDate.now(dongHoHeThong));

        // 2. Clock.fixed: Đóng băng thời gian tại 1 mốc cố định (Rất quan trọng khi viết Unit Test)
        Instant mocCoDinh = Instant.parse("2030-01-01T00:00:00Z");
        Clock dongHoDongBang = Clock.fixed(mocCoDinh, ZoneId.of("UTC"));
        System.out.println("Hôm nay (khi đóng băng): " + LocalDate.now(dongHoDongBang)); // Luôn là 2030-01-01

        // 3. Clock.offset: Tua nhanh thời gian thêm 30 ngày (test tính năng hết hạn hợp đồng, token)
        Clock dongHoTuongLai = Clock.offset(dongHoHeThong, Duration.ofDays(30));
        System.out.println("Hôm nay (30 ngày sau)  : " + LocalDate.now(dongHoTuongLai));
        // TODO 2 (mục 2, 6): Tạo Clock.fixed(Instant.parse("2026-10-19T20:00:00Z"), UTC).
        //   In LocalDate.now(clock) và LocalDateTime.now(clock). Rồi tạo clock fixed cùng
        //   instant đó nhưng zone Asia/Ho_Chi_Minh và in lại. NGÀY khác nhau - giải thích.
        //
        // TODO 3 (mục 2): Gọi clock.instant() ba lần trên một Clock.fixed, in cả ba.
        //   Xác nhận "time stands still" đúng như trang gốc nói.
        //
        // TODO 4 (mục 2, 5): Dùng Clock.offset(Clock.systemUTC(), Duration.ofDays(30)).
        //   In LocalDate.now() và LocalDate.now(clockLech). Đây là cách test "30 ngày sau".
        //
        // TODO 5 (mục 5): Dùng Clock.tickMinutes(ZoneOffset.UTC), gọi instant() vài lần
        //   cách nhau vài giây. Xác nhận giá trị không đổi trong cùng một phút.
        //
        // TODO 6 (mục 3 - BÀI TẬP CHÍNH): Viết class HoaDonService có method
        //   boolean quaHan(LocalDate ngayDenHan). Làm HAI bản:
        //   (a) dùng LocalDate.now() trực tiếp,
        //   (b) nhận Clock qua constructor và dùng LocalDate.now(clock).
        //   Rồi viết test cho cả hai. Ghi lại bản nào viết test được mà không cần mẹo.
        //
        // TODO 7 (mục 5): Tự viết lớp ClockTuaDuoc extends Clock, giữ một Instant sửa được,
        //   có method tien(Duration). Dùng nó test một luồng nhiều bước theo thời gian
        //   (ví dụ: tạo đơn -> 15 phút sau hết hạn thanh toán).
        //
        // TODO 8 (mục 4): Nếu có dự án Spring, thêm @Bean Clock và sửa một service đang gọi
        //   now() trực tiếp sang nhận Clock. Viết một test dùng Clock.fixed.
        //
        // TODO 9 (mục 7): Grep toàn bộ dự án tìm các lời gọi now() không tham số.
        //   Đếm số lượng và phân loại: chỗ nào ở tầng nghiệp vụ (cần sửa), chỗ nào ở tầng
        //   hiển thị hoặc log (chấp nhận được).
    }
}
