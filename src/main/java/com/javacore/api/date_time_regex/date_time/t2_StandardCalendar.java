package com.javacore.api.date_time_regex.date_time;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Standard Calendar
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 2/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Bài BẢN ĐỒ của cả section: một bảng tra liệt kê toàn bộ lớp temporal, mỗi dòng dẫn tới
 * một bài sau. Quay lại bài này mỗi khi quên nên dùng lớp nào.
 * - Một câu cho cả bài: chọn lớp bằng cách trả lời hai câu hỏi - THỜI GIAN MÁY hay THỜI GIAN
 * NGƯỜI, và cần BAO NHIÊU thành phần (ngày, giờ, múi giờ).
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/standard-calendar/">dev.java - Standard Calendar</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - STANDARD CALENDAR (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. HAI CÁCH BIỂU DIỄN THỜI GIAN - KHÁI NIỆM CỐT LÕI CỦA CẢ SECTION
 * - HUMAN TIME (thời gian người): năm, tháng, ngày, giờ, phút, giây. Là thứ con người đọc
 * trên lịch và đồng hồ.
 * - MACHINE TIME (thời gian máy): một dòng thời gian liên tục đo từ một mốc gốc (epoch),
 * độ phân giải nanosecond. Là một con số, không có khái niệm "tháng".
 * - Mọi lớp trong {@code java.time} thuộc về một trong hai thế giới này. Lẫn hai thế giới
 * là nguồn gốc gần như mọi bug về thời gian.
 * - {@code Instant} là thời gian máy. {@code LocalDate}, {@code LocalDateTime} là thời gian
 * người. {@code ZonedDateTime} đứng giữa: thời gian người CÓ ĐỦ thông tin để quy ra thời gian máy.
 * <p>
 * 2. BỐN CÂU HỎI ĐỂ CHỌN LỚP
 * - Cần thời gian người hay thời gian máy?
 * - Cần phần nào: chỉ ngày, chỉ giờ, hay cả hai?
 * - Có cần thông tin múi giờ không?
 * - Cần độ chi tiết tới đâu: chỉ năm, năm-tháng, tháng-ngày?
 * <p>
 * 3. BẢNG TRA LỚP TEMPORAL - PHẦN CHÍNH CỦA TRANG
 * <p>
 * .  Lớp              Chứa gì                          toString() ví dụ
 * .  Instant          giây (chính xác tới nanosecond)  2013-08-20T15:16:26.355Z
 * .  LocalDate        năm, tháng, ngày                 2013-08-20
 * .  LocalTime        giờ, phút, giây                  08:16:26.943
 * .  LocalDateTime    ngày + giờ, KHÔNG múi giờ        2013-08-20T08:16:26.937
 * .  ZonedDateTime    ngày + giờ + múi giờ + offset    2013-08-21T00:16:26.941+09:00[Asia/Tokyo]
 * .  OffsetDateTime   ngày + giờ + offset UTC          2013-08-20T08:16:26.954-07:00
 * .  OffsetTime       giờ + offset UTC                 08:16:26.957-07:00
 * .  MonthDay         tháng, ngày                      --08-20
 * .  YearMonth        năm, tháng                       2013-08
 * .  Year             năm                              2013
 * .  Month            enum tháng                       AUGUST
 * .  Duration         ngày, giờ, phút, giây            PT20H
 * .  Period           năm, tháng, ngày                 P10D
 * <p>
 * - Nhìn cột {@code toString()} là đọc ra ngay lớp chứa gì. Chú ý ba dấu hiệu:
 * + Kết thúc bằng {@code Z} là {@code Instant} - UTC, thời gian máy.
 * + Có {@code [Asia/Tokyo]} trong ngoặc vuông là {@code ZonedDateTime}, không có mà chỉ
 * có {@code +09:00} là {@code OffsetDateTime}.
 * + Bắt đầu bằng {@code P} là khoảng thời gian ({@code Period}/{@code Duration}), không
 * phải một điểm thời gian.
 * <p>
 * 4. VÍ DỤ CHỌN LỚP MÀ TRANG GỐC ĐƯA RA
 * - NGÀY SINH: dùng {@code LocalDate}. Lý do: sinh nhật được tính theo cùng một ngày lịch ở
 * mọi nơi trên thế giới, không phụ thuộc múi giờ. Đây là ví dụ kinh điển của "cố tình KHÔNG
 * mang múi giờ".
 * - SỰ KIỆN CÓ GIỜ: {@code LocalDateTime} hoặc {@code ZonedDateTime} nếu cần múi giờ.
 * - DẤU THỜI GIAN ĐỂ SO SÁNH: {@code Instant}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. CÂY QUYẾT ĐỊNH NGẮN - DÙNG CÁI NÀY Ở PRODUCTION
 * - Ghi lại một MỐC THỜI ĐIỂM đã xảy ra (createdAt, updatedAt, log, audit)? → {@code Instant}.
 * Luôn luôn. Không bàn cãi.
 * - Lịch hẹn TƯƠNG LAI mà người dùng đặt theo giờ địa phương (báo thức 7h sáng, lịch họp)?
 * → {@code LocalDateTime} + {@code ZoneId} lưu riêng. Lý do ở mục 7.
 * - Ngày thuần không có giờ (ngày sinh, ngày hóa đơn, ngày hết hạn hợp đồng)? → {@code LocalDate}.
 * - Giờ thuần lặp lại mỗi ngày (giờ mở cửa, ca làm việc)? → {@code LocalTime}.
 * - Kỳ báo cáo theo tháng? → {@code YearMonth}. Rất tiện mà ít người biết, hay bị thay bằng
 * hai biến {@code int}.
 * - Ngày lặp lại hằng năm (ngày lễ cố định, kỷ niệm)? → {@code MonthDay}.
 * <p>
 * 6. BA LỚP ÍT DÙNG NHƯNG NÊN NHỚ CÓ TỒN TẠI
 * - {@code Year}, {@code YearMonth}, {@code MonthDay} tồn tại để KHỬ PRIMITIVE OBSESSION.
 * Thay vì truyền {@code int nam, int thang} rồi lo nhầm thứ tự, truyền một {@code YearMonth}.
 * - Chúng có method nghiệp vụ sẵn: {@code Year.isLeap()}, {@code YearMonth.lengthOfMonth()},
 * {@code MonthDay.isValidYear()}. Học kỹ ở bài t4.
 * - {@code OffsetTime} gần như không bao giờ cần. Biết là có, đừng tìm cách dùng.
 * <p>
 * 7. VÌ SAO LỊCH TƯƠNG LAI KHÔNG LƯU BẰNG INSTANT - BẪY PRODUCTION THẬT
 * - Người dùng đặt lịch họp 14:00 ngày 30/03 năm sau ở Berlin. Nếu quy ngay ra {@code Instant}
 * rồi lưu, mà sau đó Đức đổi luật DST (EU đã bàn chuyện bỏ DST nhiều năm), thì khi hiển thị
 * lại nó thành 13:00 hoặc 15:00 - SAI so với ý định người dùng.
 * - Ý định người dùng là "14:00 giờ Berlin", không phải "một điểm trên dòng thời gian UTC".
 * Lưu {@code LocalDateTime} + {@code ZoneId} giữ đúng ý định đó; quy ra {@code Instant}
 * chỉ tại thời điểm cần so sánh hoặc gửi thông báo.
 * - Ngược lại: QUÁ KHỨ thì luôn lưu {@code Instant}. Chuyện đã xảy ra rồi thì nó là một
 * điểm cố định trên dòng thời gian, luật DST đổi về sau không làm nó dịch đi.
 * - Luật nhớ: QUÁ KHỨ dùng {@code Instant}, TƯƠNG LAI THEO GIỜ NGƯỜI dùng
 * {@code LocalDateTime} + {@code ZoneId}.
 * <p>
 * 8. MAP SANG KIỂU CỘT POSTGRESQL - PHẦN DÙNG HẰNG NGÀY KHI VIẾT SPRING DATA JPA
 * - {@code Instant} / {@code OffsetDateTime} → {@code timestamptz} (timestamp with time zone).
 * Đây là kiểu nên dùng cho {@code created_at}, {@code updated_at}.
 * - {@code LocalDateTime} → {@code timestamp} (không múi giờ).
 * - {@code LocalDate} → {@code date}; {@code LocalTime} → {@code time}.
 * - {@code ZonedDateTime} KHÔNG map sạch: JDBC không có kiểu tương ứng, zone id bị mất khi
 * ghi xuống. Trong entity JPA nên dùng {@code Instant} hoặc {@code OffsetDateTime}.
 * - Cảnh báo tên gọi: {@code timestamptz} của Postgres KHÔNG lưu múi giờ. Nó quy về UTC khi
 * ghi và trả về theo timezone của session khi đọc. Tên gây hiểu nhầm, nhưng hành vi đúng là
 * cái ta muốn.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - JS chỉ có MỘT lớp {@code Date} gánh hết mọi vai trong bảng mục 3. Hệ quả: phải tự quy ước
 * trong đầu "biến này đang là ngày thuần hay là mốc thời điểm", và quy ước đó không được
 * compiler kiểm tra.
 * - Java bắt chọn kiểu ngay lúc khai báo. Đổi lại phiền hơn lúc viết nhưng compiler chặn
 * được cả một lớp bug: không cộng {@code Period} vào {@code Instant} được, không so sánh
 * {@code LocalDate} với {@code LocalDateTime} được.
 * - Đây là ví dụ tốt của nguyên tắc "làm trạng thái sai trở nên không biểu diễn được" -
 * bên TS làm được bằng branded type hoặc Temporal API, Java có sẵn trong JDK.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Định nghĩa human time và machine time, mỗi cái kể hai lớp đại diện.
 * - Nhìn chuỗi {@code 2013-08-21T00:16:26.941+09:00[Asia/Tokyo]} đoán ra lớp nào, dựa vào dấu hiệu gì?
 * - Nhìn chuỗi {@code PT20H} và {@code P10D} đoán ra lớp nào?
 * - Ngày sinh nên dùng lớp nào, vì sao KHÔNG dùng lớp có múi giờ?
 * - {@code ZonedDateTime} khác {@code OffsetDateTime} ở đúng một thứ - thứ đó là gì?
 * - Cột {@code created_at} trong Postgres nên là kiểu gì, entity nên khai kiểu Java gì?
 * - Vì sao lịch họp tương lai không nên lưu thẳng bằng {@code Instant}?
 * - Kỳ báo cáo "tháng 10/2026" nên biểu diễn bằng lớp nào thay vì hai biến {@code int}?
 */
public class t2_StandardCalendar {

    void main() {
        // TODO 1 (mục 3): In toString() của cả 13 lớp trong bảng, mỗi lớp một dòng, có nhãn.
        //   Đối chiếu output thật với cột ví dụ của trang gốc. Giữ lại output này làm bảng
        //   tra cá nhân để quay lại nhìn.
        //
        System.out.println("machine time : " + Instant.now());
        System.out.println("human time   : " + LocalDateTime.now());
        // TODO 2 (mục 3): Viết hàm doanTen(String s) đoán xem chuỗi ISO đó là lớp nào, chỉ
        //   dựa vào ba dấu hiệu ở mục 3 (chữ Z cuối, ngoặc vuông, chữ P đầu). Test với
        //   năm chuỗi trong bảng.
        //
        // TODO 3 (mục 1): Lấy Instant.now() rồi in nó cùng LocalDateTime.now(). Chạy lúc
        //   buổi chiều và nhận xét vì sao hai giờ lệch nhau 7 tiếng. Giải thích bằng khái
        //   niệm human time / machine time.
        //
        // TODO 4 (mục 5): Cho năm tình huống sau, chọn lớp và viết một dòng code khai báo:
        //   (a) ngày sinh khách hàng, (b) thời điểm đơn hàng được tạo, (c) giờ mở cửa cửa hàng,
        //   (d) kỳ báo cáo doanh thu tháng, (e) ngày Quốc khánh 2/9 lặp lại hằng năm.
        //
        // TODO 5 (mục 6): Dùng YearMonth cho kỳ báo cáo: tạo YearMonth hiện tại, in
        //   lengthOfMonth(), atDay(1), atEndOfMonth(). So với cách tự tính bằng hai biến int
        //   và nhận xét.
        //
        // TODO 6 (mục 7): Dựng lại kịch bản bẫy: tạo LocalDateTime 14:00 một ngày cuối tháng 3
        //   năm sau, quy ra Instant theo ZoneId "Europe/Berlin", rồi quy ngược lại về
        //   LocalDateTime nhưng theo zone "Europe/London". In cả ba và giải thích.
        //   (Bài t6 sẽ đào sâu, ở đây chỉ cần thấy hiện tượng.)
        //
        // TODO 7 (mục 3): Thử so sánh LocalDate với LocalDateTime bằng equals() và bằng
        //   compareTo(). Cái nào compile lỗi, cái nào compile được mà trả về false? Ghi lại.
        //
        // TODO 8 (mục 8): Nếu dự án có sẵn entity JPA, tìm một cột thời gian và kiểm tra
        //   kiểu Java đang khai là gì. Nếu là Date hay ZonedDateTime thì ghi lại lý do
        //   nên đổi (chưa cần đổi thật).
    }
}
