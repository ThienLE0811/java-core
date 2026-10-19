package com.javacore.api.date_time_regex.date_time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Time Zone and Offset
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 6/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - BÀI QUAN TRỌNG NHẤT của cả section. Gần như mọi bug thời gian nghiêm trọng ở production
 * đều nằm trong phạm vi bài này.
 * - Một câu cho cả bài: OFFSET là một con số ({@code +07:00}), TIME ZONE là một BỘ LUẬT
 * ({@code Asia/Ho_Chi_Minh}) quyết định offset nào áp dụng vào lúc nào - lẫn hai thứ này
 * là gốc của mọi lỗi DST.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/zoneid-zone-offset/">dev.java - Time Zone and Offset</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - TIME ZONE AND OFFSET (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. MÚI GIỜ LÀ GÌ
 * - Một vùng địa lý dùng chung một giờ chuẩn, định danh theo dạng {@code "Asia/Tokyo"},
 * kèm một độ lệch so với UTC (ví dụ {@code +09:00}).
 * - Offset thường là số giờ chẵn, nhưng CÓ ngoại lệ (Ấn Độ {@code +05:30}, Nepal
 * {@code +05:45}). Đừng viết code giả định offset luôn chia hết cho 1 giờ.
 * <p>
 * 2. ZONEID VÀ ZONEOFFSET
 * - {@code ZoneId} "specifies a time zone identifier and provides rules for converting
 * between an {@code Instant} and a {@code LocalDateTime}". Đây là định nghĩa cốt lõi:
 * {@code ZoneId} chính là CẦU NỐI giữa thời gian máy và thời gian người.
 * - {@code ZoneOffset} "specifies a time zone offset from Greenwich/UTC time" - chỉ là
 * độ lệch, không có luật.
 * <p>
 * 3. BA LỚP MANG MÚI GIỜ
 * - {@code ZonedDateTime}: ngày + giờ + zone id + offset.
 * - {@code OffsetDateTime}: ngày + giờ + offset, KHÔNG có zone id.
 * - {@code OffsetTime}: giờ + offset, không có ngày. Trang gốc nói dùng y như
 * {@code OffsetDateTime} nhưng "when tracking the date is not needed".
 * <p>
 * 4. CHỌN OFFSETDATETIME HAY ZONEDDATETIME
 * - Dùng {@code OffsetDateTime} khi: tự quản lý luật ngày giờ, lưu timestamp xuống database
 * với offset UTC tuyệt đối, hoặc làm việc với định dạng XML / giao thức mạng yêu cầu biểu
 * diễn theo offset.
 * - KHÁC BIỆT MẤU CHỐT, nguyên văn: "only {@code ZonedDateTime} uses the {@code ZoneRules}...
 * to determine how an offset varies for a particular time zone". Tức là chỉ
 * {@code ZonedDateTime} mới xử lý được chuyển đổi daylight saving; {@code OffsetDateTime}
 * thì không.
 * <p>
 * 5. VÍ DỤ CỦA TRANG GỐC
 * - {@code ZonedDateTime}: bài toán lịch bay qua nhiều múi giờ, dùng
 * {@code withZoneSameInstant()} và {@code ZoneRules.isDaylightSavings()}.
 * - {@code OffsetDateTime}: hay đi cùng temporal adjuster như
 * {@code TemporalAdjusters.lastInMonth()} khi cần tính ngày với offset UTC cụ thể.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. OFFSET SO VỚI ZONE - HÌNH DUNG CHO NHỚ
 * - {@code ZoneOffset} = một con số đọc được trên đồng hồ lúc này: {@code +07:00}.
 * - {@code ZoneId} = cả cuốn luật của một vùng: mùa hè lệch bao nhiêu, mùa đông bao nhiêu,
 * năm nào đổi luật, lịch sử từ 1970 tới giờ.
 * - "Berlin" là {@code ZoneId}. {@code +01:00} (mùa đông) và {@code +02:00} (mùa hè) là hai
 * {@code ZoneOffset} mà luật Berlin sinh ra tùy thời điểm.
 * - Vì vậy: biết zone thì suy ra được offset tại một thời điểm; biết offset thì KHÔNG suy
 * ngược ra được zone. {@code +07:00} có thể là Việt Nam, Thái Lan, hoặc Indonesia phía tây.
 * - {@code ZoneOffset} là lớp con của {@code ZoneId} - dùng offset ở chỗ cần zone thì
 * compile được, nhưng mất hết luật DST. Đây là bẫy kiểu khá khó thấy.
 * <p>
 * 7. BA CÁCH VIẾT ZONEID - CHỌN ĐÚNG
 * - {@code ZoneId.of("Asia/Ho_Chi_Minh")} - định danh TZDB, dạng Vùng/Thành_phố. LUÔN dùng
 * dạng này ở production.
 * - {@code ZoneId.systemDefault()} - phụ thuộc máy chạy. Chỉ dùng ở tầng hiển thị, không
 * dùng trong logic nghiệp vụ. Container, CI, và laptop dev thường khác nhau ở đây.
 * - Viết tắt kiểu {@code "VST"}, {@code "EST"}, {@code "IST"} - TRÁNH. Nhập nhằng
 * ({@code IST} là Ấn Độ, Ireland hay Israel?) và đã deprecated trong {@code java.util.TimeZone}.
 * - {@code ZoneId.getAvailableZoneIds()} liệt kê hết danh sách - hơn 600 mục.
 * <p>
 * 8. BỐN METHOD CHUYỂN ĐỔI - PHẦN PHẢI THUỘC CỦA BÀI NÀY
 * - {@code localDateTime.atZone(zone)} - GẮN đồng hồ trần vào một vùng. Giờ giữ nguyên,
 * thời điểm thật được suy ra.
 * - {@code zonedDateTime.withZoneSameInstant(zone2)} - GIỮ NGUYÊN thời điểm, đổi cách đọc.
 * Giờ trên mặt đồng hồ THAY ĐỔI. Đây là cái dùng để trả lời "lúc đó bên Tokyo là mấy giờ".
 * - {@code zonedDateTime.withZoneSameLocal(zone2)} - GIỮ NGUYÊN giờ trên mặt đồng hồ, đổi
 * vùng. Thời điểm thật THAY ĐỔI. Ít dùng, nhưng là cái cần cho "cuộc họp 9h sáng giờ địa
 * phương ở mỗi văn phòng".
 * - {@code zonedDateTime.toInstant()} / {@code instant.atZone(zone)} - qua lại giữa thời
 * gian máy và thời gian người.
 * - Nhớ cặp từ: {@code SameInstant} = giữ MỐC, đổi mặt đồng hồ. {@code SameLocal} = giữ
 * MẶT ĐỒNG HỒ, đổi mốc. Nhầm hai cái này là bug lệch vài tiếng, rất khó truy.
 * <p>
 * 9. HAI NGÀY KỲ DỊ MỖI NĂM - GAP VÀ OVERLAP
 * - GAP (mùa xuân, đồng hồ nhảy tới): ở Berlin, 02:30 ngày đổi DST KHÔNG TỒN TẠI.
 * {@code atZone()} KHÔNG ném exception - nó lặng lẽ đẩy tới 03:30. Code vẫn chạy, kết quả
 * lệch một tiếng.
 * - OVERLAP (mùa thu, đồng hồ lùi lại): 02:30 xảy ra HAI LẦN. {@code atZone()} chọn lần
 * ĐẦU theo mặc định. Muốn lần sau thì {@code withLaterOffsetAtOverlap()}.
 * - Hỏi trực tiếp luật bằng {@code zone.getRules()}: {@code getValidOffsets(ldt)} trả về
 * list rỗng (gap), một phần tử (bình thường), hoặc hai phần tử (overlap).
 * - Đây là lý do trang gốc nhấn mạnh chỉ {@code ZonedDateTime} dùng {@code ZoneRules}.
 * {@code OffsetDateTime} không biết gì về gap/overlap vì nó chỉ giữ một con số.
 * - Job chạy lúc 2h sáng hằng ngày ở vùng có DST: mỗi năm một lần bị bỏ qua, một lần chạy
 * hai lần. Giải pháp thật ở production: cho scheduler chạy theo UTC, hoặc đặt giờ job
 * ngoài khoảng 1:00-3:00.
 * <p>
 * 10. QUY TẮC VÀNG CHO KIẾN TRÚC ỨNG DỤNG
 * - LƯU bằng UTC ({@code Instant} hoặc {@code OffsetDateTime} với offset {@code Z}).
 * - TÍNH TOÁN bằng UTC.
 * - CHỈ đổi sang múi giờ người dùng ở TẦNG HIỂN THỊ, sát ngoài cùng.
 * - Ngoại lệ đã nói ở bài t2 mục 7: lịch hẹn TƯƠNG LAI theo giờ địa phương thì lưu
 * {@code LocalDateTime} + {@code ZoneId} riêng, không quy sẵn ra UTC.
 * - Múi giờ của người dùng là DỮ LIỆU NGHIỆP VỤ, phải lưu trong hồ sơ người dùng - không
 * được suy từ IP hay từ timezone của server.
 * <p>
 * 11. BẪY JDBC VÀ JPA - CHỖ DEV JAVA HAY NGÃ
 * - {@code ZonedDateTime} KHÔNG map sạch xuống database: JDBC không có kiểu tương ứng,
 * zone id bị mất, chỉ còn offset. Trong entity nên dùng {@code Instant} hoặc
 * {@code OffsetDateTime}.
 * - Driver Postgres đọc {@code timestamptz} theo timezone của SESSION. Nếu JVM và database
 * khác timezone, giá trị đọc lên có thể lệch. Cách chặn: đặt
 * {@code spring.jpa.properties.hibernate.jdbc.time_zone=UTC} và cho JVM chạy UTC
 * ({@code -Duser.timezone=UTC}).
 * - Oracle thì có {@code TIMESTAMP WITH TIME ZONE} và {@code WITH LOCAL TIME ZONE}, ngữ
 * nghĩa khác Postgres. Đọc kỹ tài liệu của database đang dùng, đừng suy từ cái này sang cái kia.
 * <p>
 * 12. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Date} của JS chỉ biết UTC bên trong và timezone của trình duyệt khi hiển thị.
 * KHÔNG lưu được "thời điểm này theo giờ Tokyo". Muốn làm phải dùng {@code Intl} hoặc
 * thư viện như luxon.
 * - {@code toLocaleString('vi-VN', {timeZone: 'Asia/Ho_Chi_Minh'})} ≈
 * {@code withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"))} rồi format - nhưng bên JS chỉ
 * là chuyện HIỂN THỊ, giá trị gốc không đổi kiểu. Bên Java nó là một đối tượng có kiểu riêng.
 * - {@code Temporal.ZonedDateTime} của TC39 cuối cùng cũng mang đúng mô hình của Java:
 * instant + zone id + calendar.
 * - {@code Intl.DateTimeFormat().resolvedOptions().timeZone} ≈ {@code ZoneId.systemDefault()},
 * và cùng một lời khuyên: đừng tin nó trong logic nghiệp vụ.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 13. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code ZoneId} khác {@code ZoneOffset} ở điểm cốt lõi nào?
 * - Vì sao từ offset {@code +07:00} không suy ngược ra được zone?
 * - {@code ZoneOffset} là lớp con của {@code ZoneId} - điều đó tạo ra bẫy gì?
 * - Nêu đúng một thứ {@code ZonedDateTime} làm được mà {@code OffsetDateTime} không làm được.
 * - Phân biệt {@code withZoneSameInstant} với {@code withZoneSameLocal} bằng một câu mỗi cái.
 * - {@code atZone()} với một giờ rơi vào gap DST thì ném exception hay trả về gì?
 * - Overlap thì {@code atZone()} chọn offset nào, đổi bằng method gì?
 * - Hỏi luật DST của một zone bằng API nào?
 * - Quy tắc vàng lưu/tính/hiển thị thời gian gồm ba vế nào?
 * - Vì sao không nên để {@code ZonedDateTime} trong entity JPA?
 * - Múi giờ người dùng nên lấy từ đâu, KHÔNG nên lấy từ đâu?
 */
public class t6_TimeZoneAndOffset {

    void main() {
        // TODO 1 (mục 7): In ZoneId.systemDefault() và ZoneId.getAvailableZoneIds().size().
        //   Lọc danh sách lấy các zone bắt đầu bằng "Asia/" và in ra, đếm số lượng.
        //
        LocalDateTime hop = LocalDateTime.of(2026, 10, 19, 14, 0);   // 14:00 tran, chua biet o dau

        ZonedDateTime oVN = hop.atZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime oTokyo = oVN.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));

        // TODO 2 (mục 6): Với Instant.now(), in offset hiện tại của ba zone:
        //   Asia/Ho_Chi_Minh, Europe/Berlin, Asia/Kathmandu. Chú ý cái thứ ba không chẵn giờ.
        //
        // TODO 3 (mục 8): Lấy LocalDateTime 14:00 hôm nay. Gọi atZone("Asia/Ho_Chi_Minh"),
        //   rồi withZoneSameInstant("Asia/Tokyo") và withZoneSameLocal("Asia/Tokyo").
        //   In cả ba. Viết một câu giải thích vì sao hai cái sau khác nhau.
        //
        // TODO 4 (mục 8): Từ kết quả TODO 3, gọi toInstant() trên cả ba. Xác nhận cái nào
        //   bằng nhau, cái nào không. Đây là bài kiểm chứng SameInstant / SameLocal.
        //
        // TODO 5 (mục 9 - GAP): Tìm ngày đổi DST mùa xuân của Europe/Berlin năm nay
        //   (gợi ý: zone.getRules().getTransitions() hoặc nextTransition).
        //   Gọi LocalDateTime 02:30 ngày đó .atZone(berlin). In kết quả.
        //   Nó KHÔNG ném exception - ghi lại nó nhảy sang mấy giờ.
        //
        // TODO 6 (mục 9 - OVERLAP): Làm tương tự với ngày đổi DST mùa thu. Gọi
        //   getRules().getValidOffsets(ldt) và in list - xác nhận có HAI phần tử.
        //   Rồi so sánh atZone() mặc định với withLaterOffsetAtOverlap().
        //
        // TODO 7 (mục 9): Với cùng một LocalDateTime 12:00 giữa tháng 6 và giữa tháng 12,
        //   in offset của Europe/Berlin. Xác nhận +02:00 và +01:00. Rồi làm lại với
        //   Asia/Ho_Chi_Minh và giải thích vì sao không đổi.
        //
        // TODO 8 (mục 6): Viết ZoneId.of("+07:00") và ZoneId.of("Asia/Ho_Chi_Minh").
        //   In getClass() của cả hai. Rồi dùng cả hai để tính giờ Berlin vào tháng 6 và
        //   tháng 12 - chỉ ra cái nào sai.
        //
        // TODO 9 (mục 10): Viết class LichHen với ba field: LocalDateTime gioDiaPhuong,
        //   ZoneId mui, Instant taoLuc. Viết method gioTheoMui(ZoneId nguoiXem) trả về
        //   ZonedDateTime để hiển thị. Đây là mô hình nên dùng ở production.
        //
        // TODO 10 (mục 9): Viết hàm trả về thời điểm chạy tiếp theo của một job "2h sáng
        //   hằng ngày" ở Europe/Berlin, chạy đúng cả vào ngày gap. Giải thích cách xử lý
        //   bạn chọn và vì sao.
        //
        // TODO 11 (mục 11): In TimeZone.getDefault().getID() và ZoneId.systemDefault().
        //   Chạy lại với -Duser.timezone=UTC. Ghi lại khác biệt - đây đúng là chuyện xảy ra
        //   khi deploy vào container.
    }
}
