/**
 * Bài 11 - {@link java.lang.Comparable} và {@link java.util.Comparator}: hai cách trả lời câu
 * hỏi "cái nào đứng trước", và lý do Java cần tới cả hai.
 *
 * <p>Khác bài 10 ở một điểm căn bản. Ở đó {@code Chargeable} và {@code Connectable} là
 * interface do ta tự định nghĩa, ta vừa viết hợp đồng vừa cài đặt nó. Ở đây hợp đồng do JDK
 * viết sẵn từ hai mươi năm trước và cả thư viện chuẩn đã dựa vào nó - {@code List.sort},
 * {@code TreeSet}, {@code Collections.max}. Việc của ta là <em>tôn trọng</em> một hợp đồng
 * người khác đặt ra, kể cả những điều khoản không ai ép kiểm tra: trả đúng dấu, ném
 * {@code NullPointerException} khi nhận {@code null}, và giữ tính bắc cầu. Đây là tình huống
 * thường gặp hơn nhiều so với tự thiết kế interface.
 *
 * <p>Vì sao cần hai interface chứ không phải một:
 * <ul>
 *   <li>{@link java.lang.Comparable} - thứ tự <em>mặc định</em>, viết bên trong class. Chỉ có
 *       một method nên mỗi class cài được đúng một kiểu so sánh. Ở đây là theo giá</li>
 *   <li>{@link java.util.Comparator} - thứ tự <em>cho một lần dùng cụ thể</em>, viết bên
 *       ngoài, muốn bao nhiêu cái cũng được. Ở đây là theo tên, theo tồn kho, và cách ghép
 *       nhiều tiêu chí</li>
 * </ul>
 *
 * <p>Ba cái bẫy mà bài này tồn tại để dựng lên, cả ba đều được chốt bằng test:
 * <ol>
 *   <li>Trừ tay thay vì {@link java.lang.Double#compare} - {@code (int) (a - b)} nuốt mất
 *       phần lẻ với {@code double}, và tràn số với {@code int}</li>
 *   <li>{@link java.util.Comparator#reversed()} dán vào cuối chuỗi đảo <em>cả cụm</em> chứ
 *       không riêng tiêu chí cuối. Muốn đảo một tiêu chí thì đảo ngay tại chỗ nó, trước khi
 *       nối {@code thenComparing}</li>
 *   <li>{@code compareTo} không nhất quán với {@code equals} - {@code Product} so theo giá
 *       nên hai sản phẩm khác nhau cùng giá cho 0, và {@code TreeSet} nuốt mất một cái.
 *       {@code SapXepSanPhamDemo} không chạm tới chuyện này, nhưng
 *       {@code ProductTest} thì có</li>
 * </ol>
 *
 * <p>Quy ước đặt tên trong package: giữ {@code Product} theo tên tiếng Anh của đề bài, nhưng
 * field, method và hằng {@code Comparator} thì tiếng Việt như phần còn lại của
 * {@code javacore33}. {@code THEO_TEN} và {@code THEO_STOCK} đặt {@code public static final}
 * ngay trong {@code Product} theo idiom của chính JDK - xem
 * {@link java.lang.String#CASE_INSENSITIVE_ORDER}.
 *
 * <p>{@code SapXepSanPhamDemo} có {@code main} và in ra sáu thứ tự khác nhau trên cùng một dữ
 * liệu. Chạy nó rồi so cặp (4) với (5) là thấy ngay cái bẫy {@code reversed()}, nhanh hơn đọc
 * mô tả.
 */
package com.javacore.practice.javacore33.oop.sanpham;
