package com.javacore.api.date_time_regex.date_time;

import java.time.LocalDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.ThaiBuddhistDate;

/**
 * Non-ISO Date Conversion
 * <p>
 * 0. Vị trí bài này
 * - Bước 24/41, bài 12/13 trong section The Date Time API của lộ trình dev.java/learn.
 * - Bài NGẮN và ÍT DÙNG NHẤT section. Trang gốc tự nói nó không đi sâu vào
 * {@code java.time.chrono}. Đọc để biết có tồn tại, không cần thuộc.
 * - Một câu cho cả bài: hệ lịch khác chỉ đổi CÁCH ĐỌC ngày, không đổi điểm trên dòng thời
 * gian - nên chuyển đổi hai chiều đều qua {@code from(TemporalAccessor)}.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/date-time/non-iso-conversion/">dev.java - Non-ISO Date Conversion</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - NON-ISO DATE CONVERSION (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. PHẠM VI
 * - Nguyên văn: "This tutorial does not discuss the {@code java.time.chrono} package in any
 * detail. However, it might be useful to know that this package provides several predefined
 * chronologies that are not ISO-based, such as Japanese, Hijrah, Minguo, and Thai Buddhist."
 * - Tức là trang này chỉ dạy ĐÚNG MỘT việc: chuyển qua lại giữa lịch ISO và lịch khác.
 * <p>
 * 2. CHUYỂN SANG LỊCH KHÔNG PHẢI ISO
 * - (Converting to a Non-ISO-Based Date)
 * - "You can convert an ISO-based date to a date in another chronology by using the
 * {@code from(TemporalAccessor)} factory method, such as
 * {@code JapaneseDate.from(TemporalAccessor)}."
 * - "This method throws a {@code DateTimeException} if it is unable to convert the date to a
 * valid instance."
 * - Ví dụ trang gốc: từ một {@code LocalDateTime} chuyển sang các hệ lịch khác nhau.
 * <p>
 * 3. CHUYỂN VỀ LỊCH ISO
 * - (Converting to an ISO-Based Date)
 * - "You can convert from a non-ISO date to a {@code LocalDate} instance using the static
 * {@code LocalDate.from()} method."
 * - "Other temporal-based classes also provide this method, which throws a
 * {@code DateTimeException} if the date cannot be converted."
 * - Ví dụ trang gốc: một method {@code fromString()} parse chuỗi chứa ngày không ISO và trả
 * về một {@code LocalDate}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. BỐN HỆ LỊCH CÓ SẴN - BIẾT ĐỂ KHÔNG NGẠC NHIÊN
 * - {@code JapaneseDate} - lịch Nhật theo NIÊN HIỆU (Reiwa, Heisei, Showa). Năm 2026 là
 * Reiwa 8. Niên hiệu đổi khi đổi Thiên hoàng, nên dữ liệu này cập nhật theo JDK - chính là
 * lý do JDK phải vá khi Nhật đổi sang Reiwa năm 2019.
 * - {@code HijrahDate} - lịch Hồi giáo, theo mặt trăng. Một năm khoảng 354 ngày nên nó TRÔI
 * so với lịch dương. Dùng ở Ả Rập Xê Út và nhiều nước Hồi giáo.
 * - {@code MinguoDate} - lịch Dân Quốc (Đài Loan), năm 1 = 1912. Chỉ là lịch ISO lệch 1911 năm.
 * - {@code ThaiBuddhistDate} - lịch Phật giáo Thái, năm 1 = 543 TCN. Cũng chỉ lệch 543 năm.
 * - Tất cả đều cài {@code ChronoLocalDate}, nên dùng chung được với {@code TemporalAdjuster}
 * của bài t9 và {@code DateTimeFormatter} của bài t8.
 * <p>
 * 5. CHÚ Ý VỀ LỊCH ÂM VIỆT NAM
 * - JDK KHÔNG có lịch âm Việt Nam và cũng không có lịch âm Trung Quốc.
 * - Lịch âm Việt Nam khác lịch âm Trung Quốc ở múi giờ tính sóc/khí (UTC+7 so với UTC+8),
 * nên có năm Tết lệch một ngày. Không có thư viện chuẩn nào xử lý đúng chuyện này.
 * - Dự án cần tính Tết, ngày giỗ, ngày rằm thì phải dùng thư viện ngoài hoặc tự cài thuật
 * toán (thuật toán Hồ Ngọc Đức là cái hay được dùng ở Việt Nam).
 * - Nếu tự cài thì gói nó thành một {@code Chronology}/{@code ChronoLocalDate} riêng để
 * dùng được chung với phần còn lại của {@code java.time} - đây đúng là nguyên tắc
 * "Extensible" ở bài t1 mục 2.
 * <p>
 * 6. BẪY SO SÁNH GIỮA CÁC HỆ LỊCH
 * - {@code ChronoLocalDate} CỐ TÌNH không cho so sánh trực tiếp hai ngày khác hệ lịch bằng
 * {@code equals()} - javadoc của nó cảnh báo rất dài về chuyện này.
 * - Lý do: cùng một ngày viết theo hai lịch thì {@code equals()} trả về {@code false} vì
 * khác {@code Chronology}, dù chúng là cùng một ngày thật.
 * - Cách đúng: quy cả hai về {@code LocalDate} rồi so, hoặc so bằng {@code toEpochDay()}.
 * - Luật chung cho ứng dụng: LƯU TRỮ và TÍNH TOÁN luôn bằng ISO ({@code LocalDate}),
 * chỉ chuyển sang lịch khác ở TẦNG HIỂN THỊ. Giống hệt quy tắc vàng về múi giờ ở bài t6 mục 10.
 * <p>
 * 7. CÁCH DÙNG THỰC TẾ DUY NHẤT HAY GẶP
 * - Hiển thị ngày theo lịch bản địa cho người dùng ở Nhật, Thái, Ả Rập.
 * - Không cần tự chuyển: {@code DateTimeFormatter} nhận {@code Chronology} qua
 * {@code withChronology()}, hoặc dùng locale có đuôi {@code -u-ca-japanese}.
 * Cách này sạch hơn tự gọi {@code JapaneseDate.from()}.
 * - {@code Chronology.ofLocale(locale)} trả về hệ lịch phù hợp với locale - tiện khi ứng dụng
 * đa ngôn ngữ và không muốn viết điều kiện cho từng nước.
 * <p>
 * 8. ĐỐI CHIẾU TYPESCRIPT
 * - JS giải bằng {@code Intl}: {@code toLocaleDateString('ja-JP-u-ca-japanese')} - chỉ là
 * chuyện FORMAT, không có kiểu dữ liệu riêng cho lịch Nhật.
 * - Java cho hẳn các lớp {@code JapaneseDate}, {@code HijrahDate}... nên tính toán ĐƯỢC trên
 * hệ lịch đó (cộng tháng theo lịch Hồi giáo cho kết quả khác cộng tháng theo lịch dương).
 * Bên JS muốn làm vậy phải dùng thư viện ngoài.
 * - {@code Temporal} của TC39 có tham số {@code calendar} trên mọi kiểu Plain, gần với mô
 * hình {@code Chronology} của Java hơn.
 * - Cả hai hệ sinh thái đều cùng một lời khuyên: lưu bằng ISO, đổi lịch khi hiển thị.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn hệ lịch không-ISO mà trang gốc kể tên là gì?
 * - Chuyển từ {@code LocalDate} sang {@code JapaneseDate} bằng method nào? Ném exception gì khi hỏng?
 * - Chiều ngược lại dùng method nào?
 * - Vì sao không nên so sánh trực tiếp hai {@code ChronoLocalDate} khác hệ lịch?
 * - Cách đúng để so sánh hai ngày thuộc hai hệ lịch khác nhau là gì?
 * - Luật lưu trữ/hiển thị với hệ lịch giống luật nào đã học ở bài t6?
 * - JDK có lịch âm Việt Nam không? Nếu cần thì làm sao?
 * - Cách sạch nhất để HIỂN THỊ ngày theo lịch Nhật là gì - tự convert hay dùng formatter?
 */
public class t12_NonIsoDateConversion {

    void main() {
        // TODO 1 (mục 2, 4): Lấy LocalDate hôm nay, chuyển sang cả bốn hệ lịch
        //   (JapaneseDate, HijrahDate, MinguoDate, ThaiBuddhistDate) bằng from().
        //   In toString() từng cái và đối chiếu năm với lịch dương.
        LocalDate homNay = LocalDate.now();
        System.out.println("1. Lịch chuẩn ISO (Dương lịch): " + homNay);

        // ========================================================
        // Chiều 1: Chuyển từ LocalDate sang hệ lịch khác
        // ========================================================
        JapaneseDate lichNhat = JapaneseDate.from(homNay);
        ThaiBuddhistDate lichThai = ThaiBuddhistDate.from(homNay);

        System.out.println("2. Lịch Nhật (Niên hiệu Reiwa) : " + lichNhat);
        System.out.println("3. Lịch Phật giáo Thái Lan     : " + lichThai);

        // ========================================================
        // Chiều 2: Chuyển ngược lại về LocalDate chuẩn ISO
        // ========================================================
        LocalDate quayLaiIso = LocalDate.from(lichNhat);
        System.out.println("4. Quy đổi ngược về ISO       : " + quayLaiIso);
        // TODO 2 (mục 4): Kiểm chứng ba phép lệch đơn giản: Minguo = ISO - 1911,
        //   ThaiBuddhist = ISO + 543. Còn Japanese và Hijrah thì không theo công thức -
        //   giải thích vì sao.
        //
        // TODO 3 (mục 3): Chuyển ngược cả bốn về LocalDate bằng LocalDate.from().
        //   Xác nhận tất cả về đúng ngày ban đầu.
        //
        // TODO 4 (mục 4): Với JapaneseDate, in getEra() và get(ChronoField.YEAR_OF_ERA).
        //   Rồi thử với một ngày trước 2019 và xem niên hiệu đổi thế nào.
        //
        // TODO 5 (mục 6 - BẪY): So sánh JapaneseDate.from(d).equals(d) với d là LocalDate.
        //   Dự đoán kết quả trước khi chạy. Rồi so bằng toEpochDay() và đối chiếu.
        //
        // TODO 6 (mục 4): Cộng 1 tháng vào HijrahDate và vào LocalDate của cùng một ngày,
        //   rồi quy cả hai về LocalDate. In số ngày chênh lệch và giải thích.
        //
        // TODO 7 (mục 7): Format ngày hôm nay bằng DateTimeFormatter có
        //   withChronology(JapaneseChronology.INSTANCE) và locale ja-JP.
        //   So với cách tự gọi JapaneseDate.from() rồi format.
        //
        // TODO 8 (mục 7): Gọi Chronology.ofLocale() với vài locale (ja-JP-u-ca-japanese,
        //   th-TH, en-US) và in kết quả.
        //
        // TODO 9 (mục 2): Thử from() với một TemporalAccessor KHÔNG có phần ngày
        //   (ví dụ LocalTime). Dán lại exception.
    }
}
